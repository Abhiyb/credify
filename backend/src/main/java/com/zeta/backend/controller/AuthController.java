package com.zeta.backend.controller;

import com.zeta.backend.model.UserProfile;
import com.zeta.backend.repository.UserProfileRepository;
import com.zeta.backend.service.implementation.EmailService;
import com.zeta.backend.util.OtpUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserProfileRepository userProfileRepository;
    private final OtpUtil otpUtil;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;   // ← changed to interface

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        if (email == null || email.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Email is required");
        }

        UserProfile user = userProfileRepository.findByEmail(email)
                .orElse(null);

        if (user == null) {
            return ResponseEntity.badRequest().body("Email not registered. Please sign up.");
        }

        String otp = otpUtil.generateOtp(email);
        emailService.sendOtp(email, otp);
        return ResponseEntity.ok("OTP sent to your email");
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOtp(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String otp   = request.get("otp");

        if (email == null || otp == null) {
            return ResponseEntity.badRequest().body("Email and OTP are required");
        }

        boolean valid = otpUtil.validateOtp(email, otp);
        if (!valid) {
            return ResponseEntity.badRequest().body("Invalid or expired OTP");
        }

        return ResponseEntity.ok("OTP verified. You can now reset your password.");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody Map<String, String> request) {
        String email       = request.get("email");
        String newPassword = request.get("newPassword");

        if (email == null || newPassword == null || newPassword.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Email and new password are required");
        }

        UserProfile user = userProfileRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        user.setPassword(passwordEncoder.encode(newPassword));
        userProfileRepository.save(user);

        return ResponseEntity.ok("Password updated successfully");
    }
}