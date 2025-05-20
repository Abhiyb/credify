package com.zeta.backend.service;

import com.zeta.backend.model.UserProfile;
import com.zeta.backend.repository.UserProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserProfileServiceImpl implements UserProfileService {

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Override
    public UserProfile createProfile(UserProfile userProfile) {

        return userProfileRepository.save(userProfile);
    }

    @Override
    public Optional<UserProfile> getProfile(Long userId) {
        return userProfileRepository.findById(userId);
    }

    @Override
    public UserProfile updateProfile(Long userId, UserProfile updatedProfile) {
        Optional<UserProfile> existing = userProfileRepository.findById(userId);

        if (existing.isPresent()) {
            UserProfile profile = existing.get();

            profile.setFullName(updatedProfile.getFullName());
            profile.setEmail(updatedProfile.getEmail());
            profile.setPhone(updatedProfile.getPhone());
            profile.setAddress(updatedProfile.getAddress());
            profile.setAnnualIncome(updatedProfile.getAnnualIncome());
            profile.setIsEligibleForBNPL(updatedProfile.getIsEligibleForBNPL());

            return userProfileRepository.save(profile);
        } else {
            // Handle profile not found as per your app logic
            throw new RuntimeException("User profile not found");
        }
    }
}
