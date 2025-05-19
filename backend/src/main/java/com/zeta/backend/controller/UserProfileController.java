package com.zeta.backend.controller;

import com.zeta.backend.model.UserProfile;
import com.zeta.backend.service.UserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/profile")
public class UserProfileController {

    @Autowired
    private UserProfileService userProfileService;

    @GetMapping("{userId}")
    public ResponseEntity<UserProfile> getUserProfile(@PathVariable Long userId) {
        UserProfile profile = userProfileService.getUserProfileByUserId(userId);
        return ResponseEntity.ok(profile);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<UserProfile> updateUserProfile(
            @PathVariable Long userId,
            @RequestBody Map<String, Object> updates) {

        String phone = (String) updates.get("phone");
        String address = (String) updates.get("address");

        // Make sure to handle possible type conversion
        Double annualIncome = null;
        if (updates.get("annualIncome") != null) {
            annualIncome = Double.valueOf(updates.get("annualIncome").toString());
        }

        UserProfile updatedProfile = userProfileService.updateUserProfile(userId, phone, address, annualIncome);
        return ResponseEntity.ok(updatedProfile);

    }

}