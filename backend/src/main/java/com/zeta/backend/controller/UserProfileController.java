package com.zeta.backend.controller;

import com.zeta.backend.dto.UserProfileCreateDTO;
import com.zeta.backend.dto.UserProfileUpdateDTO;
import com.zeta.backend.dto.UserProfileResponseDTO;
import com.zeta.backend.model.UserProfile;
import com.zeta.backend.repository.UserProfileRepository;
import com.zeta.backend.service.UserProfileService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/profile")
@Slf4j
public class UserProfileController {

    @Autowired
    private UserProfileRepository userProfileRepository;

     @Autowired
     private UserProfileService userProfileService;
    // ========================== CREATE PROFILE ==========================
    // Handles POST requests to create a new user profile
    @PostMapping
    public ResponseEntity<?> createProfile(@Valid @RequestBody UserProfileCreateDTO userProfile) {
        log.info("Attempting to create user profile: {}", userProfile.getEmail());

        // Validate presence of password
        if (userProfile.getPassword() == null || userProfile.getPassword().isBlank()) {
            log.warn("Password is missing for user creation.");
            return ResponseEntity.badRequest().body("Password is required");
        }

        // Validate password strength
        if (!userProfile.getPassword().matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).{8,}$")) {
            log.warn("Invalid password format for user: {}", userProfile.getEmail());
            return ResponseEntity.badRequest().body(
                    "Password must be at least 8 characters long and include one uppercase letter, one lowercase letter, one digit, and one special character (@#$%^&+=!)");
        }

        // Duplicate validations
        if (userProfileRepository.existsByEmail(userProfile.getEmail())) {
            log.warn("Email already exists: {}", userProfile.getEmail());
            return ResponseEntity.badRequest().body("Email already exists, please try a different one.");
        }
        if (userProfileRepository.existsByPhone(userProfile.getPhone())) {
            log.warn("Phone already exists: {}", userProfile.getPhone());
            return ResponseEntity.badRequest().body("Phone number already exists, please try a different one.");
        }
        if (userProfileRepository.existsByFullName(userProfile.getFullName())) {
            log.warn("Full name already exists: {}", userProfile.getFullName());
            return ResponseEntity.badRequest().body("Full name already exists, please try a different one.");
        }

        // Map CreateDTO to Entity
        UserProfile entity = UserProfile.builder()
                .fullName(userProfile.getFullName())
                .email(userProfile.getEmail())
                .phone(userProfile.getPhone())
                .address(userProfile.getAddress())
                .annualIncome(userProfile.getAnnualIncome())
                .password(userProfile.getPassword())
                .build();

        // Save entity
        UserProfile saved = userProfileRepository.save(entity);

        // Build response DTO
        UserProfileResponseDTO response = UserProfileResponseDTO.builder()
                .userId(saved.getUserId())
                .fullName(saved.getFullName())
                .email(saved.getEmail())
                .phone(saved.getPhone())
                .address(saved.getAddress())
                .annualIncome(saved.getAnnualIncome())
                .isEligibleForBNPL(saved.getIsEligibleForBNPL())
                .createdAt(saved.getCreatedAt())
                .updatedAt(saved.getUpdatedAt())
                .build();

        log.info("User profile created successfully with ID: {}", saved.getUserId());
        return ResponseEntity.ok(response);
    }

    // ========================== GET PROFILE BY ID ==========================
    @GetMapping("/{userId}")
    public ResponseEntity<?> getProfile(@PathVariable Long userId) {
        log.info("Fetching profile for userId: {}", userId);
        Optional<UserProfile> profile = userProfileRepository.findById(userId);

        return profile.map(data -> {
            UserProfileResponseDTO response = UserProfileResponseDTO.builder()
                    .userId(data.getUserId())
                    .fullName(data.getFullName())
                    .email(data.getEmail())
                    .phone(data.getPhone())
                    .address(data.getAddress())
                    .annualIncome(data.getAnnualIncome())
                    .isEligibleForBNPL(data.getIsEligibleForBNPL())
                    .createdAt(data.getCreatedAt())
                    .updatedAt(data.getUpdatedAt())
                    .build();

            log.info("Profile found for userId: {}", userId);
            return ResponseEntity.ok(response);
        }).orElseGet(() -> {
            log.warn("Profile not found for userId: {}", userId);
            return ResponseEntity.status(404).build();
        });
    }

    // ========================== GET ALL PROFILES ==========================
    @GetMapping
    public ResponseEntity<List<UserProfileResponseDTO>> getAllProfiles() {
        log.info("Fetching all user profiles");

        List<UserProfile> profiles = userProfileRepository.findAll();
        List<UserProfileResponseDTO> response = profiles.stream()
                .map(data -> UserProfileResponseDTO.builder()
                        .userId(data.getUserId())
                        .fullName(data.getFullName())
                        .email(data.getEmail())
                        .phone(data.getPhone())
                        .address(data.getAddress())
                        .annualIncome(data.getAnnualIncome())
                        .isEligibleForBNPL(data.getIsEligibleForBNPL())
                        .build()
                ).toList();
        return ResponseEntity.ok(response);
    }

    // ========================== UPDATE PROFILE ==========================
    @PutMapping("/{userId}")
    public ResponseEntity<?> updateProfile(@PathVariable Long userId,
                                           @Valid @RequestBody UserProfileUpdateDTO updatedProfile) {
        log.info("Attempting to update profile for userId: {}", userId);
        Optional<UserProfile> existing = userProfileRepository.findById(userId);

        if (existing.isPresent()) {
            UserProfile profile = existing.get();

            // Check for duplicate fields (if changed)
            if (updatedProfile.getEmail() != null && !profile.getEmail().equals(updatedProfile.getEmail()) &&
                    userProfileRepository.existsByEmail(updatedProfile.getEmail())) {
                log.warn("Email already exists during update: {}", updatedProfile.getEmail());
                return ResponseEntity.badRequest().body("Email already exists, please try a different one.");
            }

            if (updatedProfile.getPhone() != null && !profile.getPhone().equals(updatedProfile.getPhone()) &&
                    userProfileRepository.existsByPhone(updatedProfile.getPhone())) {
                log.warn("Phone already exists during update: {}", updatedProfile.getPhone());
                return ResponseEntity.badRequest().body("Phone number already exists, please try a different one.");
            }

            if (updatedProfile.getFullName() != null && !profile.getFullName().equals(updatedProfile.getFullName()) &&
                    userProfileRepository.existsByFullName(updatedProfile.getFullName())) {
                log.warn("Full name already exists during update: {}", updatedProfile.getFullName());
                return ResponseEntity.badRequest().body("Full name already exists, please try a different one.");
            }

            // Update provided fields (excluding password, eligibility)
            if (updatedProfile.getFullName() != null) {
                profile.setFullName(updatedProfile.getFullName());
            }
            if (updatedProfile.getEmail() != null) {
                profile.setEmail(updatedProfile.getEmail());
            }
            if (updatedProfile.getPhone() != null) {
                profile.setPhone(updatedProfile.getPhone());
            }
            if (updatedProfile.getAddress() != null) {
                profile.setAddress(updatedProfile.getAddress());
            }
            if (updatedProfile.getAnnualIncome() != null) {
                profile.setAnnualIncome(updatedProfile.getAnnualIncome());
            }

            // Save updated profile
            UserProfile saved = userProfileRepository.save(profile);

            // Map to response DTO
            UserProfileResponseDTO response = UserProfileResponseDTO.builder()
                    .userId(saved.getUserId())
                    .fullName(saved.getFullName())
                    .email(saved.getEmail())
                    .phone(saved.getPhone())
                    .address(saved.getAddress())
                    .annualIncome(saved.getAnnualIncome())
                    .isEligibleForBNPL(saved.getIsEligibleForBNPL())
                    .createdAt(saved.getCreatedAt())
                    .updatedAt(saved.getUpdatedAt())
                    .build();

            log.info("User profile updated successfully for userId: {}", userId);
            return ResponseEntity.ok(response);
        } else {
            log.warn("User profile not found for update. userId: {}", userId);
            return ResponseEntity.status(404).body("User profile not found.");
        }
    }

    // ========================== LOGIN ==========================
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String password = credentials.get("password");

        Optional<UserProfile> userOpt = userProfileRepository.findByEmail(email);

        if (userOpt.isEmpty()) {
            log.warn("Login failed - user not found: {}", email);
            return ResponseEntity.status(404).body("User doesn't exist");
        }

        UserProfile user = userOpt.get();
        log.info("Login attempt for user: {}", user.getFullName());

        if (!user.getPassword().equals(password)) {
            log.warn("Login failed - invalid password for user: {}", user.getFullName());
            return ResponseEntity.status(401).body("Invalid password");
        }

        log.info("Login successful for userId: {}", user.getUserId());
        return ResponseEntity.ok(Map.of(
                "message", "Login successful",
                "userId", user.getUserId()
        ));
    }

    // ========================== BNPL ELIGIBILITY ==========================
    @GetMapping("/{userId}/bnpl-eligibility")
    public ResponseEntity<?> checkBnplEligibility(@PathVariable Long userId) {
        log.info("Checking BNPL eligibility for userId: {}", userId);
        Optional<UserProfile> userOpt = userProfileRepository.findById(userId);

        if (userOpt.isEmpty()) {
            log.warn("BNPL check failed - user not found: {}", userId);
            return ResponseEntity.status(404).body("User not found");
        }

        Boolean eligible = userOpt.get().getIsEligibleForBNPL();
        log.info("BNPL eligibility for userId {}: {}", userId, eligible);
        return ResponseEntity.ok(Map.of(
                "userId", userId,
                "isEligibleForBNPL", eligible
        ));
    }
    // ========================== UPDATE PASSWORD ==========================
    @PutMapping("/{userId}/password")
    public ResponseEntity<?> updatePassword(@PathVariable Long userId, @RequestBody Map<String, String> passwordData) {
        log.info("Attempting to update password for userId: {}", userId);

        // Extract password from request body
        String newPassword = passwordData.get("password");

        // Validate password presence
        if (newPassword == null || newPassword.isBlank()) {
            log.warn("Password is missing for userId: {}", userId);
            return ResponseEntity.badRequest().body("Password is required");
        }

        // Validate password strength
        if (!newPassword.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).{8,}$")) {
            log.warn("Invalid password format for userId: {}", userId);
            return ResponseEntity.badRequest().body(
                    "Password must be at least 8 characters long and include one uppercase letter, one lowercase letter, one digit, and one special character (@#$%^&+=!)");
        }

        try {
            userProfileService.updatePassword(userId, newPassword);
            log.info("Password updated successfully for userId: {}", userId);
            return ResponseEntity.ok(Map.of("message", "Password updated successfully"));
        } catch (RuntimeException e) {
            log.warn("Failed to update password for userId: {}. Error: {}", userId, e.getMessage());
            return ResponseEntity.status(404).body("User not found");
        }
    }
}