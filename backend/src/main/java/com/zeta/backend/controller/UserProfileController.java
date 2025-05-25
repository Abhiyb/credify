package com.zeta.backend.controller;

import com.zeta.backend.dto.UserProfileCreateDTO;
import com.zeta.backend.dto.UserProfileUpdateDTO;
import com.zeta.backend.dto.UserProfileResponseDTO;
import com.zeta.backend.model.UserProfile;
import com.zeta.backend.repository.UserProfileRepository;
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

    // Handles POST requests to create a new user profile
    @PostMapping
    public ResponseEntity<?> createProfile(@Valid @RequestBody UserProfileCreateDTO userProfile) {
        log.info("Attempting to create user profile: {}", userProfile.getEmail());

        // Validate that a password is provided
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

        // Check for duplicate email
        if (userProfileRepository.existsByEmail(userProfile.getEmail())) {
            log.warn("Email already exists: {}", userProfile.getEmail());
            return ResponseEntity.badRequest().body("Email already exists, please try a different one.");
        }

        // Check for duplicate phone number
        if (userProfileRepository.existsByPhone(userProfile.getPhone())) {
            log.warn("Phone already exists: {}", userProfile.getPhone());
            return ResponseEntity.badRequest().body("Phone number already exists, please try a different one.");
        }

        // Check for duplicate full name
        if (userProfileRepository.existsByFullName(userProfile.getFullName())) {
            log.warn("Full name already exists: {}", userProfile.getFullName());
            return ResponseEntity.badRequest().body("Full name already exists, please try a different one.");
        }

        // Check for duplicate password
        if (userProfileRepository.existsByPassword(userProfile.getPassword())) {
            log.warn("Password already in use.");
            return ResponseEntity.badRequest().body("Password already exists, please try a different one.");
        }

        // Map UserProfileCreateDTO to UserProfile entity
        UserProfile entity = new UserProfile();
        entity.setFullName(userProfile.getFullName());
        entity.setEmail(userProfile.getEmail());
        entity.setPhone(userProfile.getPhone());
        entity.setAddress(userProfile.getAddress());
        entity.setAnnualIncome(userProfile.getAnnualIncome());
        entity.setPassword(userProfile.getPassword());

        // Save the entity
        UserProfile saved = userProfileRepository.save(entity);

        // Map to UserProfileResponseDTO
        UserProfileResponseDTO response = new UserProfileResponseDTO();
        response.setUserId(saved.getUserId());
        response.setFullName(saved.getFullName());
        response.setEmail(saved.getEmail());
        response.setPhone(saved.getPhone());
        response.setAddress(saved.getAddress());
        response.setAnnualIncome(saved.getAnnualIncome());
        response.setIsEligibleForBNPL(saved.getIsEligibleForBNPL());
        response.setCreatedAt(saved.getCreatedAt());
        response.setUpdatedAt(saved.getUpdatedAt());

        log.info("User profile created successfully with ID: {}", saved.getUserId());
        return ResponseEntity.ok(response);
    }

    // Handles GET requests to retrieve a user profile by ID
    @GetMapping("/{userId}")
    public ResponseEntity<?> getProfile(@PathVariable Long userId) {
        log.info("Fetching profile for userId: {}", userId);
        Optional<UserProfile> profile = userProfileRepository.findById(userId);

        return profile.map(data -> {
            UserProfileResponseDTO response = new UserProfileResponseDTO();
            response.setUserId(data.getUserId());
            response.setFullName(data.getFullName());
            response.setEmail(data.getEmail());
            response.setPhone(data.getPhone());
            response.setAddress(data.getAddress());
            response.setAnnualIncome(data.getAnnualIncome());
            response.setIsEligibleForBNPL(data.getIsEligibleForBNPL());
            response.setCreatedAt(data.getCreatedAt());
            response.setUpdatedAt(data.getUpdatedAt());

            log.info("Profile found for userId: {}", userId);
            return ResponseEntity.ok(response);
        }).orElseGet(() -> {
            log.warn("Profile not found for userId: {}", userId);
            return ResponseEntity.status(404).build();
        });
    }

    // Handles GET requests to retrieve all user profiles
    @GetMapping
    public ResponseEntity<List<UserProfileResponseDTO>> getAllProfiles() {
        log.info("Fetching all user profiles");
        List<UserProfile> profiles = userProfileRepository.findAll();
        List<UserProfileResponseDTO> response = profiles.stream().map(data -> {
            UserProfileResponseDTO dto = new UserProfileResponseDTO();
            dto.setUserId(data.getUserId());
            dto.setFullName(data.getFullName());
            dto.setEmail(data.getEmail());
            dto.setPhone(data.getPhone());
            dto.setAddress(data.getAddress());
            dto.setAnnualIncome(data.getAnnualIncome());
            dto.setIsEligibleForBNPL(data.getIsEligibleForBNPL());

            return dto;
        }).toList();
        return ResponseEntity.ok(response);
    }

    // Handles PUT requests to update an existing user profile by ID
    @PutMapping("/{userId}")
    public ResponseEntity<?> updateProfile(@PathVariable Long userId,
                                           @Valid @RequestBody UserProfileUpdateDTO updatedProfile) {
        log.info("Attempting to update profile for userId: {}", userId);
        Optional<UserProfile> existing = userProfileRepository.findById(userId);

        if (existing.isPresent()) {
            UserProfile profile = existing.get();

            // Prevent duplicate email if changed
            if (updatedProfile.getEmail() != null && !profile.getEmail().equals(updatedProfile.getEmail()) &&
                    userProfileRepository.existsByEmail(updatedProfile.getEmail())) {
                log.warn("Email already exists during update: {}", updatedProfile.getEmail());
                return ResponseEntity.badRequest().body("Email already exists, please try a different one.");
            }

            // Prevent duplicate phone number if changed
            if (updatedProfile.getPhone() != null && !profile.getPhone().equals(updatedProfile.getPhone()) &&
                    userProfileRepository.existsByPhone(updatedProfile.getPhone())) {
                log.warn("Phone already exists during update: {}", updatedProfile.getPhone());
                return ResponseEntity.badRequest().body("Phone number already exists, please try a different one.");
            }

            // Prevent duplicate full name if changed
            if (updatedProfile.getFullName() != null && !profile.getFullName().equals(updatedProfile.getFullName()) &&
                    userProfileRepository.existsByFullName(updatedProfile.getFullName())) {
                log.warn("Full name already exists during update: {}", updatedProfile.getFullName());
                return ResponseEntity.badRequest().body("Full name already exists, please try a different one.");
            }

            // Update fields if provided (excluding password and isEligibleForBNPL)
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
            // Password is updated only via /password endpoint
            // isEligibleForBNPL is computed server-side via @PreUpdate, ignoring dto value

            // Save the updated profile
            UserProfile saved = userProfileRepository.save(profile);

            // Map to UserProfileResponseDTO
            UserProfileResponseDTO response = new UserProfileResponseDTO();
            response.setUserId(saved.getUserId());
            response.setFullName(saved.getFullName());
            response.setEmail(saved.getEmail());
            response.setPhone(saved.getPhone());
            response.setAddress(saved.getAddress());
            response.setAnnualIncome(saved.getAnnualIncome());
            response.setIsEligibleForBNPL(saved.getIsEligibleForBNPL());
            response.setCreatedAt(saved.getCreatedAt());
            response.setUpdatedAt(saved.getUpdatedAt());

            log.info("User profile updated successfully for userId: {}", userId);
            return ResponseEntity.ok(response);
        } else {
            log.warn("User profile not found for update. userId: {}", userId);
            return ResponseEntity.status(404).body("User profile not found.");
        }
    }

    // Handles POST requests for user login
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String password = credentials.get("password");

        Optional<UserProfile> userOpt = userProfileRepository.findByEmail(email);

        if (userOpt.isEmpty()) {
            log.warn("Login failed - user not found: {}", email);
            return ResponseEntity.status(404).body("User doesn't exist");  // return here
        }

        UserProfile user = userOpt.get();

        log.info("Login attempt for user: {}", user.getFullName());

        if (!user.getPassword().equals(password)) {
            log.warn("Login failed - invalid password for user: {}", user.getFullName());
            return ResponseEntity.status(401).body("Invalid password");  // return here
        }

        log.info("Login successful for userId: {}", user.getUserId());
        return ResponseEntity.ok(Map.of(
                "message", "Login successful",
                "userId", user.getUserId()
        ));  // return here
    }

    // Handles GET requests to check BNPL eligibility for a user
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
                "eligible", eligible,
                "message", eligible ? "You are eligible for BNPL" : "You are not eligible for BNPL"
        ));
    }

    // Handles PUT requests to update a user’s password
    @PutMapping("/{userId}/password")
    public ResponseEntity<?> updatePassword(@PathVariable Long userId,
                                            @RequestBody Map<String, String> passwordData) {
        log.info("Password update attempt for userId: {}", userId);
        String newPassword = passwordData.get("password");

        if (newPassword == null || newPassword.isBlank()) {
            log.warn("Password update failed - missing password for userId: {}", userId);
            return ResponseEntity.badRequest().body("Password is required");
        }

        if (!newPassword.matches("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$")) {
            log.warn("Password update failed - weak password for userId: {}", userId);
            return ResponseEntity.badRequest().body(
                    "Password must be at least 8 characters long and include uppercase, lowercase, number, and special character");
        }

        if (userProfileRepository.existsByPassword(newPassword)) {
            log.warn("Password already in use during update for userId: {}", userId);
            return ResponseEntity.badRequest().body("Password already exists, please try a different one");
        }

        Optional<UserProfile> optionalUser = userProfileRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            log.warn("Password update failed - user not found: {}", userId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        UserProfile existingUser = optionalUser.get();
        existingUser.setPassword(newPassword);
        UserProfile saved = userProfileRepository.save(existingUser);

        UserProfileResponseDTO response = new UserProfileResponseDTO();
        response.setUserId(saved.getUserId());
        response.setFullName(saved.getFullName());
        response.setEmail(saved.getEmail());
        response.setPhone(saved.getPhone());
        response.setAddress(saved.getAddress());
        response.setAnnualIncome(saved.getAnnualIncome());
        response.setIsEligibleForBNPL(saved.getIsEligibleForBNPL());
        response.setCreatedAt(saved.getCreatedAt());
        response.setUpdatedAt(saved.getUpdatedAt());

        log.info("Password updated successfully for userId: {}", userId);
        return ResponseEntity.ok(Map.of(
                "message", "Password updated successfully",
                "user", response
        ));
    }
}