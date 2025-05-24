package com.zeta.backend.controller;

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

    @PostMapping
    public ResponseEntity<?> createProfile(@Valid @RequestBody UserProfile userProfile) {
        log.info("Attempting to create user profile: {}", userProfile.getEmail());

        // Ensure password is provided
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

        // Check for duplicate email, phone, full name, and password
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

        if (userProfileRepository.existsByPassword(userProfile.getPassword())) {
            log.warn("Password already in use.");
            return ResponseEntity.badRequest().body("Password already exists, please try a different one.");
        }

        UserProfile saved = userProfileRepository.save(userProfile);
        log.info("User profile created successfully with ID: {}", saved.getUserId());
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> getProfile(@PathVariable Long userId) {
        log.info("Fetching profile for userId: {}", userId);
        Optional<UserProfile> profile = userProfileRepository.findById(userId);

        return profile.map(data -> {
            log.info("Profile found for userId: {}", userId);
            return ResponseEntity.ok(data);
        }).orElseGet(() -> {
            log.warn("Profile not found for userId: {}", userId);
            return ResponseEntity.status(404).build();
        });
    }

    @GetMapping
    public ResponseEntity<List<UserProfile>> getAllProfiles() {
        log.info("Fetching all user profiles");
        List<UserProfile> profiles = userProfileRepository.findAll();
        return ResponseEntity.ok(profiles);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<?> updateProfile(@PathVariable Long userId,
                                           @Valid @RequestBody UserProfile updatedProfile) {
        log.info("Attempting to update profile for userId: {}", userId);
        Optional<UserProfile> existing = userProfileRepository.findById(userId);

        if (existing.isPresent()) {
            UserProfile profile = existing.get();

            // Prevent duplicate email if changed
            if (!profile.getEmail().equals(updatedProfile.getEmail()) &&
                    userProfileRepository.existsByEmail(updatedProfile.getEmail())) {
                log.warn("Email already exists during update: {}", updatedProfile.getEmail());
                return ResponseEntity.badRequest().body("Email already exists, please try a different one.");
            }

            // Prevent duplicate phone if changed
            if (!profile.getPhone().equals(updatedProfile.getPhone()) &&
                    userProfileRepository.existsByPhone(updatedProfile.getPhone())) {
                log.warn("Phone already exists during update: {}", updatedProfile.getPhone());
                return ResponseEntity.badRequest().body("Phone number already exists, please try a different one.");
            }

            // Prevent duplicate full name if changed
            if (!profile.getFullName().equals(updatedProfile.getFullName()) &&
                    userProfileRepository.existsByFullName(updatedProfile.getFullName())) {
                log.warn("Full name already exists during update: {}", updatedProfile.getFullName());
                return ResponseEntity.badRequest().body("Full name already exists, please try a different one.");
            }

            // Update allowed fields
            profile.setFullName(updatedProfile.getFullName());
            profile.setEmail(updatedProfile.getEmail());
            profile.setPhone(updatedProfile.getPhone());
            profile.setAddress(updatedProfile.getAddress());
            profile.setAnnualIncome(updatedProfile.getAnnualIncome());

            UserProfile saved = userProfileRepository.save(profile);
            log.info("User profile updated successfully for userId: {}", userId);
            return ResponseEntity.ok(saved);
        } else {
            log.warn("User profile not found for update. userId: {}", userId);
            return ResponseEntity.status(404).body("User profile not found.");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String password = credentials.get("password");

        log.info("Login attempt for email: {}", email);
        Optional<UserProfile> userOpt = userProfileRepository.findByEmail(email);

        if (userOpt.isEmpty()) {
            log.warn("Login failed - user not found: {}", email);
            return ResponseEntity.status(404).body("User doesn't exist");
        }

        UserProfile user = userOpt.get();
        if (!user.getPassword().equals(password)) {
            log.warn("Login failed - invalid password for email: {}", email);
            return ResponseEntity.status(401).body("Invalid password");
        }

        log.info("Login successful for userId: {}", user.getUserId());
        return ResponseEntity.ok(Map.of(
                "message", "Login successful",
                "userId", user.getUserId()
        ));
    }

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

    @PutMapping("/{userId}/password")
    public ResponseEntity<?> updatePassword(@PathVariable Long userId,
                                            @RequestBody UserProfile updatedProfile) {
        log.info("Password update attempt for userId: {}", userId);
        Optional<UserProfile> optionalUser = userProfileRepository.findById(userId);

        if (optionalUser.isEmpty()) {
            log.warn("Password update failed - user not found: {}", userId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        String newPassword = updatedProfile.getPassword();

        // Validate presence of new password
        if (newPassword == null || newPassword.isBlank()) {
            log.warn("Password update failed - missing password for userId: {}", userId);
            return ResponseEntity.badRequest().body("Password is required");
        }

        // Enforce strong password policy
        if (!newPassword.matches("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$")) {
            log.warn("Password update failed - weak password for userId: {}", userId);
            return ResponseEntity.badRequest().body(
                    "Password must be at least 8 characters long and include uppercase, lowercase, number, and special character");
        }

        if (userProfileRepository.existsByPassword(newPassword)) {
            log.warn("Password already in use during update for userId: {}", userId);
            return ResponseEntity.badRequest().body("Password already exists, please try a different one");
        }

        UserProfile existingUser = optionalUser.get();
        existingUser.setPassword(newPassword);
        userProfileRepository.save(existingUser);
        log.info("Password updated successfully for userId: {}", userId);
        return ResponseEntity.ok("Password updated successfully");
    }
}