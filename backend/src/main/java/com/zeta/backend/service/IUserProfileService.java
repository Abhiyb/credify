package com.zeta.backend.service;

import com.zeta.backend.model.UserProfile;

import java.util.Optional;

public interface IUserProfileService {

    UserProfile createProfile(UserProfile userProfile);

    Optional<UserProfile> getProfile(Long userId);

    UserProfile updateProfile(Long userId, UserProfile updatedProfile);

    void updatePassword(Long userId, String newPassword);

    void deleteProfile(Long userId);
}
