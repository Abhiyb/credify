package com.zeta.backend.controller;

import com.zeta.backend.model.UserProfile;
import com.zeta.backend.repository.UserProfileRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/profile")
public class UserProfileController {

    @Autowired
    private UserProfileRepository userProfileRepository;

    // Create new profile with uniqueness checks
    @PostMapping
    public ResponseEntity<?> createProfile(@Valid @RequestBody UserProfile userProfile) {

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

            if (!profile.getPassword().equals(updatedProfile.getPassword()) &&
                    userProfileRepository.existsByPassword(updatedProfile.getPassword())) {
                return ResponseEntity.badRequest().body("Password already exists, please try a different one.");
            }

            profile.setFullName(updatedProfile.getFullName());
            profile.setEmail(updatedProfile.getEmail());
            profile.setPhone(updatedProfile.getPhone());
            profile.setAddress(updatedProfile.getAddress());
            profile.setAnnualIncome(updatedProfile.getAnnualIncome());
            profile.setPassword(updatedProfile.getPassword());

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

        // Successful login
        return ResponseEntity.ok(Map.of("message", "Login successful"));
    }
}
