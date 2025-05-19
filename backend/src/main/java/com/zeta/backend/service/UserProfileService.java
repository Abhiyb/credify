package com.zeta.backend.service;

import com.zeta.backend.model.User;
import com.zeta.backend.model.UserProfile;
import com.zeta.backend.repository.UserProfileRepository;
import com.zeta.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserProfileService {

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private UserRepository userRepository;

    public UserProfile getUserProfileByUserId(Long userId) {
        return userProfileRepository.findByUserId(userId)
                .orElseGet(() -> {
                    UserProfile up = new UserProfile();
                    userRepository.findById(userId).ifPresent(up::setUser);
                    return up;
                });
    }
    public UserProfile updateUserProfile(Long userId, String phone, String address, Double annualIncome) {
        UserProfile profile = userProfileRepository.findByUserId(userId).orElse(new UserProfile());
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        profile.setUser(user);
        profile.setPhone(phone);
        profile.setAddress(address);
        profile.setAnnualIncome(annualIncome);

        UserProfile savedProfile = userProfileRepository.save(profile);

        // Sync to User
        user.setPhoneNumber(phone);
        user.setAddress(address);
        user.setIsEligibleForBNPL(annualIncome >= 50000);
        userRepository.save(user);

        return savedProfile;
    }

}