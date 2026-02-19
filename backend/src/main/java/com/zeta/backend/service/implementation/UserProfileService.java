package com.zeta.backend.service.implementation;

import com.zeta.backend.dto.UserProfileCreateDTO;
import com.zeta.backend.dto.UserProfileUpdateDTO;
import com.zeta.backend.dto.UserProfileResponseDTO;
import com.zeta.backend.model.UserProfile;
import com.zeta.backend.repository.UserProfileRepository;
import com.zeta.backend.security.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@Service
@Slf4j
public class UserProfileService {

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;




    // ========================== CREATE PROFILE ==========================
    public UserProfileResponseDTO createProfile(UserProfileCreateDTO dto) {

        if (dto.getPassword() == null || dto.getPassword().isBlank()) {
            throw new RuntimeException("Password is required");
        }

        if (userProfileRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        if (userProfileRepository.existsByPhone(dto.getPhone())) {
            throw new RuntimeException("Phone number already exists");
        }

        if (userProfileRepository.existsByFullName(dto.getFullName())) {
            throw new RuntimeException("Full name already exists");
        }

        UserProfile entity = UserProfile.builder()
                .fullName(dto.getFullName())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .address(dto.getAddress())
                .annualIncome(dto.getAnnualIncome())
                .password(passwordEncoder.encode(dto.getPassword()))
                .build();

        UserProfile saved = userProfileRepository.save(entity);

        return UserProfileResponseDTO.builder()
                .userId(saved.getUserId())
                .fullName(saved.getFullName())
                .email(saved.getEmail())
                .phone(saved.getPhone())
                .address(saved.getAddress())
                .annualIncome(saved.getAnnualIncome())
                .isEligibleForBNPL(saved.getIsEligibleForBNPL())
                .createdAt(saved.getCreatedAt())
                .updatedAt(saved.getUpdatedAt())
                .build();
    }

    // ========================== LOGIN ==========================
    public Map<String, Object> login(String email, String password) {

        UserProfile user = userProfileRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User doesn't exist"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        String token = jwtUtil.generateToken(user.getEmail());

        return Map.of(
                "message", "Login successful",
                "userId", user.getUserId(),
                "token", token
        );
    }

    // ========================== GET PROFILE ==========================
    public UserProfileResponseDTO getMyProfile(String email) {

        UserProfile profile = userProfileRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return UserProfileResponseDTO.builder()
                .userId(profile.getUserId())
                .fullName(profile.getFullName())
                .email(profile.getEmail())
                .phone(profile.getPhone())
                .address(profile.getAddress())
                .annualIncome(profile.getAnnualIncome())
                .isEligibleForBNPL(profile.getIsEligibleForBNPL())
                .createdAt(profile.getCreatedAt())
                .updatedAt(profile.getUpdatedAt())
                .build();
    }

    // ========================== UPDATE PROFILE ==========================
    public Map<String, Object> updateMyProfile(String email, UserProfileUpdateDTO dto) {

        UserProfile profile = userProfileRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (dto.getFullName() != null) profile.setFullName(dto.getFullName());
        if (dto.getPhone() != null) profile.setPhone(dto.getPhone());
        if (dto.getAddress() != null) profile.setAddress(dto.getAddress());
        if (dto.getAnnualIncome() != null) profile.setAnnualIncome(dto.getAnnualIncome());

        UserProfile saved = userProfileRepository.save(profile);

        return Map.of(
                "message", "Profile updated successfully",
                "userId", saved.getUserId()
        );
    }

    // ========================== DELETE PROFILE ==========================
    public Map<String, Object> deleteMyProfile(String email) {

        UserProfile profile = userProfileRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        userProfileRepository.delete(profile);

        return Map.of("message", "User profile deleted successfully");
    }

    // ========================== BNPL ELIGIBILITY ==========================
    public Map<String, Object> checkMyBnplEligibility(String email) {

        UserProfile profile = userProfileRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return Map.of(
                "email", email,
                "isEligibleForBNPL", profile.getIsEligibleForBNPL()
        );
    }

    // ========================== UPDATE PASSWORD ==========================
    public Map<String, Object> updateMyPassword(String email, String oldPassword, String newPassword) {

        UserProfile user = userProfileRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Old password is incorrect");

        }

        if (oldPassword.equals(newPassword)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "New password cannot be same as old password");
        }


        user.setPassword(passwordEncoder.encode(newPassword));
        userProfileRepository.save(user);

        return Map.of("message", "Password updated successfully");
    }
}
