package com.zeta.backend.service;

import com.zeta.backend.model.UserProfile;
import com.zeta.backend.repository.UserProfileRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class UserProfileService implements IUserProfileService {

    @Autowired
    private UserProfileRepository userProfileRepository;

    // Create a new profile
    @Override
    public UserProfile createProfile(UserProfile userProfile) {
        log.info("Creating new user profile for email: {}", userProfile.getEmail());
        return userProfileRepository.save(userProfile);
    }

    // Get profile by userId
    @Override
    public Optional<UserProfile> getProfile(Long userId) {
        log.info("Fetching profile for userId: {}", userId);
        return userProfileRepository.findById(userId);
    }

    // Update existing user profile
    @Override
    public UserProfile updateProfile(Long userId, UserProfile updatedProfile) {
        log.info("Updating user profile for userId: {}", userId);
        Optional<UserProfile> existingOpt = userProfileRepository.findById(userId);

        if (existingOpt.isPresent()) {
            UserProfile profile = existingOpt.get();

            // Update fields
            profile.setFullName(updatedProfile.getFullName());
            profile.setEmail(updatedProfile.getEmail());
            profile.setPhone(updatedProfile.getPhone());
            profile.setAddress(updatedProfile.getAddress());
            profile.setAnnualIncome(updatedProfile.getAnnualIncome());
            profile.setIsEligibleForBNPL(updatedProfile.getIsEligibleForBNPL());
            profile.setPassword(updatedProfile.getPassword());

            log.info("User profile updated successfully for userId: {}", userId);
            return userProfileRepository.save(profile);
        } else {
            log.warn("User profile not found for update. userId: {}", userId);
            throw new RuntimeException("User profile not found");
        }
    }

    // Update password for a given userId
    public UserProfile updatePassword(Long userId, String newPassword) {
        log.info("Attempting password update for userId: {}", userId);
        Optional<UserProfile> optionalUser = userProfileRepository.findById(userId);

        if (optionalUser.isEmpty()) {
            log.warn("Password update failed - user not found: {}", userId);
            throw new RuntimeException("User not found");
        }

        UserProfile user = optionalUser.get();
        user.setPassword(newPassword);

        log.info("Password updated successfully for userId: {}", userId);
        return userProfileRepository.save(user);
    }
}