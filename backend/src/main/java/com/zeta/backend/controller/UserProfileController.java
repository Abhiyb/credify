package com.zeta.backend.controller;

import com.zeta.backend.model.UserProfile;
import com.zeta.backend.repository.UserProfileRepository;
import jakarta.validation.Valid;

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
public class UserProfileController {

    @Autowired
    private UserProfileRepository userProfileRepository;

    // Create new profile with uniqueness checks and password validation
    @PostMapping
    public ResponseEntity<?> createProfile(@Valid @RequestBody UserProfile userProfile) {
        System.out.println("Received user: " + userProfile);

        // Validate password for creation
        if (userProfile.getPassword() == null || userProfile.getPassword().isBlank()) {
            return ResponseEntity.badRequest().body("Password is required");
        }
        if (!userProfile.getPassword().matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).{8,}$")) {
            return ResponseEntity.badRequest().body(
                    "Password must be at least 8 characters long and include one uppercase letter, one lowercase letter, one digit, and one special character (@#$%^&+=!)");
        }

        if (userProfileRepository.existsByEmail(userProfile.getEmail())) {
            return ResponseEntity.badRequest().body("Email already exists, please try a different one.");
        }

        if (userProfileRepository.existsByPhone(userProfile.getPhone())) {
            return ResponseEntity.badRequest().body("Phone number already exists, please try a different one.");
        }

        if (userProfileRepository.existsByFullName(userProfile.getFullName())) {
            return ResponseEntity.badRequest().body("Full name already exists, please try a different one.");
        }

        if (userProfileRepository.existsByPassword(userProfile.getPassword())) {
            return ResponseEntity.badRequest().body("Password already exists, please try a different one.");
        }

        UserProfile saved = userProfileRepository.save(userProfile);
        return ResponseEntity.ok(saved);
    }

    // Get profile by userId
    @GetMapping("/{userId}")
    public ResponseEntity<?> getProfile(@PathVariable Long userId) {
        Optional<UserProfile> profile = userProfileRepository.findById(userId);
        return profile.map(data -> ResponseEntity.ok(data))
                .orElseGet(() -> ResponseEntity.status(404).build());
    }

    // Get all profiles
    @GetMapping
    public ResponseEntity<List<UserProfile>> getAllProfiles() {
        List<UserProfile> profiles = userProfileRepository.findAll();
        return ResponseEntity.ok(profiles);
    }

    // Update profile by userId
    @PutMapping("/{userId}")
    public ResponseEntity<?> updateProfile(@PathVariable Long userId,
                                           @Valid @RequestBody UserProfile updatedProfile) {
        Optional<UserProfile> existing = userProfileRepository.findById(userId);
        if (existing.isPresent()) {
            UserProfile profile = existing.get();

            // Check for uniqueness on update except for current record itself
            if (!profile.getEmail().equals(updatedProfile.getEmail()) &&
                    userProfileRepository.existsByEmail(updatedProfile.getEmail())) {
                return ResponseEntity.badRequest().body("Email already exists, please try a different one.");
            }

            if (!profile.getPhone().equals(updatedProfile.getPhone()) &&
                    userProfileRepository.existsByPhone(updatedProfile.getPhone())) {
                return ResponseEntity.badRequest().body("Phone number already exists, please try a different one.");
            }

            if (!profile.getFullName().equals(updatedProfile.getFullName()) &&
                    userProfileRepository.existsByFullName(updatedProfile.getFullName())) {
                return ResponseEntity.badRequest().body("Full name already exists, please try a different one.");
            }

            // Update fields (excluding password)
            profile.setFullName(updatedProfile.getFullName());
            profile.setEmail(updatedProfile.getEmail());
            profile.setPhone(updatedProfile.getPhone());
            profile.setAddress(updatedProfile.getAddress());
            profile.setAnnualIncome(updatedProfile.getAnnualIncome());

            UserProfile saved = userProfileRepository.save(profile);
            return ResponseEntity.ok(saved);
        } else {
            return ResponseEntity.status(404).body("User profile not found.");
        }
    }

    // DTO for login request
    public static class LoginRequest {
        private String email;
        private String password;

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    // Login endpoint
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        Optional<UserProfile> userOpt = userProfileRepository.findByEmail(loginRequest.getEmail());

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(404).body("User doesn't exist");
        }

        UserProfile user = userOpt.get();
        if (!user.getPassword().equals(loginRequest.getPassword())) {
            return ResponseEntity.status(401).body("Invalid password");
        }

        return ResponseEntity.ok(Map.of(
                "message", "Login successful",
                "userId", user.getUserId()
        ));
    }

    // Check BNPL eligibility
    @GetMapping("/{userId}/bnpl-eligibility")
    public ResponseEntity<?> checkBnplEligibility(@PathVariable Long userId) {
        Optional<UserProfile> userOpt = userProfileRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(404).body("User not found");
        }
        UserProfile user = userOpt.get();
        Boolean eligible = user.getIsEligibleForBNPL();
        return ResponseEntity.ok(Map.of(
                "eligible", eligible,
                "message", eligible ? "You are eligible for BNPL" : "You are not eligible for BNPL"));
    }

    // Update password endpoint
    @PutMapping("/{userId}/password")
    public ResponseEntity<?> updatePassword(@PathVariable Long userId,
                                            @RequestBody UserProfile updatedProfile) {
        Optional<UserProfile> optionalUser = userProfileRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        UserProfile existingUser = optionalUser.get();

        String newPassword = updatedProfile.getPassword();

        if (newPassword == null || newPassword.isBlank()) {
            return ResponseEntity.badRequest().body("Password is required");
        }

        if (!newPassword.matches("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$")) {
            return ResponseEntity.badRequest().body(
                    "Password must be at least 8 characters long and include uppercase, lowercase, number, and special character");
        }

        if (userProfileRepository.existsByPassword(newPassword)) {
            return ResponseEntity.badRequest().body("Password already exists, please try a different one");
        }

        existingUser.setPassword(newPassword);
        userProfileRepository.save(existingUser);

        return ResponseEntity.ok("Password updated successfully");
    }
}