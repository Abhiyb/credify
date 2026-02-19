package com.zeta.backend.controller;

import com.zeta.backend.dto.UserProfileCreateDTO;
import com.zeta.backend.dto.UserProfileUpdateDTO;
import com.zeta.backend.service.implementation.UserProfileService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/profile")
@Slf4j
public class UserProfileController {

    @Autowired
    private UserProfileService userProfileService;

    // ========================== CREATE PROFILE ==========================
    @PostMapping
    public ResponseEntity<?> createProfile(@Valid @RequestBody UserProfileCreateDTO dto) {
        return ResponseEntity.ok(userProfileService.createProfile(dto));
    }

    // ========================== LOGIN ==========================
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {

        String email = credentials.get("email");
        String password = credentials.get("password");

        return ResponseEntity.ok(userProfileService.login(email, password));
    }

    // ========================== GET MY PROFILE ==========================
    @GetMapping("/me")
    public ResponseEntity<?> getMyProfile(Authentication authentication) {

        String email = authentication.getName();
        return ResponseEntity.ok(userProfileService.getMyProfile(email));
    }

    // ========================== UPDATE MY PROFILE ==========================
    @PutMapping("/me")
    public ResponseEntity<?> updateMyProfile(Authentication authentication,
                                             @Valid @RequestBody UserProfileUpdateDTO dto) {

        String email = authentication.getName();
        return ResponseEntity.ok(userProfileService.updateMyProfile(email, dto));
    }

    // ========================== DELETE MY PROFILE ==========================
    @DeleteMapping("/me")
    public ResponseEntity<?> deleteMyProfile(Authentication authentication) {

        String email = authentication.getName();
        return ResponseEntity.ok(userProfileService.deleteMyProfile(email));
    }

    // ========================== BNPL ELIGIBILITY ==========================
    @GetMapping("/me/bnpl-eligibility")
    public ResponseEntity<?> checkMyBnplEligibility(Authentication authentication) {

        String email = authentication.getName();
        return ResponseEntity.ok(userProfileService.checkMyBnplEligibility(email));
    }

    // ========================== UPDATE PASSWORD ==========================
    @PutMapping("/me/password")
    public ResponseEntity<?> updateMyPassword(Authentication authentication,
                                              @RequestBody Map<String, String> passwordData) {

        String email = authentication.getName();
        String oldPassword = passwordData.get("oldPassword");
        String newPassword = passwordData.get("newPassword");

        return ResponseEntity.ok(userProfileService.updateMyPassword(email, oldPassword, newPassword));
    }
}
