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
            UserProfile existingProfile = existingOpt.get();

            // Build a new UserProfile instance with updated values but keep unchanged fields from existingProfile
            UserProfile updated = UserProfile.builder()
                    .userId(existingProfile.getUserId())  // keep the same userId (primary key)
                    .fullName(updatedProfile.getFullName())
                    .email(updatedProfile.getEmail())
                    .phone(updatedProfile.getPhone())
                    .address(updatedProfile.getAddress())
                    .annualIncome(updatedProfile.getAnnualIncome())
                    .isEligibleForBNPL(updatedProfile.getIsEligibleForBNPL())
                    .password(updatedProfile.getPassword())
                    .build();

            log.info("User profile updated successfully for userId: {}", userId);
            return userProfileRepository.save(updated);

        } else {
            log.warn("User profile not found for update. userId: {}", userId);
            throw new RuntimeException("User profile not found");
        }
    }
}