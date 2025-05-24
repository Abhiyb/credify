package com.zeta.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zeta.backend.model.UserProfile;
import com.zeta.backend.repository.UserProfileRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.*;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for UserProfileController endpoints.
 */
@Slf4j
public class UserProfileControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserProfileRepository userProfileRepository;

    @InjectMocks
    private UserProfileController userProfileController;

    private ObjectMapper objectMapper = new ObjectMapper();
    private UserProfile mockUser;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userProfileController).build();

        // Create mock user profile with dummy data
        mockUser = new UserProfile();
        mockUser.setUserId(1L);
        mockUser.setFullName("John Doe");
        mockUser.setEmail("john@example.com");
        mockUser.setPhone("1234567890");
        mockUser.setAddress("123 Street");
        mockUser.setAnnualIncome(500000.0);
        mockUser.setPassword("Pass@1234");
        mockUser.setIsEligibleForBNPL(true);

        log.info("Test setup completed with mock user: {}", mockUser);
    }

    // Test case: successful profile creation
    @Test
    public void testCreateProfile_Success() throws Exception {
        log.info("Testing profile creation - valid data");

        // Mock validations
        when(userProfileRepository.existsByEmail(anyString())).thenReturn(false);
        when(userProfileRepository.existsByPhone(anyString())).thenReturn(false);
        when(userProfileRepository.existsByFullName(anyString())).thenReturn(false);
        when(userProfileRepository.existsByPassword(anyString())).thenReturn(false);
        when(userProfileRepository.save(any(UserProfile.class))).thenReturn(mockUser);

        // Convert mock user to JSON
        String jsonRequest = objectMapper.writeValueAsString(mockUser);

        // Perform POST request
        mockMvc.perform(post("/api/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk());

        log.info("Profile creation test passed");
    }

    // Test case: missing password should return 400 Bad Request
    @Test
    public void testCreateProfile_MissingPassword() throws Exception {
        log.info("Testing profile creation - missing password");

        mockUser.setPassword("");  // Set empty password
        String jsonRequest = objectMapper.writeValueAsString(mockUser);

        mockMvc.perform(post("/api/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest());

        log.info("Handled missing password correctly");
    }

    // Test case: successful fetch of profile
    @Test
    public void testGetProfile_Success() throws Exception {
        log.info("Testing get profile - valid userId");

        when(userProfileRepository.findById(1L)).thenReturn(Optional.of(mockUser));

        mockMvc.perform(get("/api/profile/1"))
                .andExpect(status().isOk());

        log.info("Profile fetch successful");
    }

    // Test case: fetch non-existent profile should return 404
    @Test
    public void testGetProfile_NotFound() throws Exception {
        log.info("Testing get profile - user not found");

        when(userProfileRepository.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/profile/1"))
                .andExpect(status().isNotFound());

        log.info("Correctly handled user not found case");
    }

    // Test case: successful update of profile
    @Test
    public void testUpdateProfile_Success() throws Exception {
        log.info("Testing update profile - valid data");

        // Mock find and validations
        when(userProfileRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(userProfileRepository.existsByEmail(anyString())).thenReturn(false);
        when(userProfileRepository.existsByPhone(anyString())).thenReturn(false);
        when(userProfileRepository.existsByFullName(anyString())).thenReturn(false);
        when(userProfileRepository.save(any(UserProfile.class))).thenReturn(mockUser);

        String jsonRequest = objectMapper.writeValueAsString(mockUser);

        mockMvc.perform(put("/api/profile/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk());

        log.info("Profile update test passed");
    }

    // Test case: successful login with valid credentials
    @Test
    public void testLogin_Success() throws Exception {
        log.info("Testing login - valid credentials");

        Map<String, String> credentials = new HashMap<>();
        credentials.put("email", "john@example.com");
        credentials.put("password", "Pass@1234");

        when(userProfileRepository.findByEmail("john@example.com")).thenReturn(Optional.of(mockUser));

        mockMvc.perform(post("/api/profile/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(credentials)))
                .andExpect(status().isOk());

        log.info("Login success case passed");
    }

    // Test case: login with wrong password should return 401 Unauthorized
    @Test
    public void testLogin_InvalidPassword() throws Exception {
        log.info("Testing login - invalid password");

        Map<String, String> credentials = new HashMap<>();
        credentials.put("email", "john@example.com");
        credentials.put("password", "WrongPass");

        when(userProfileRepository.findByEmail("john@example.com")).thenReturn(Optional.of(mockUser));

        mockMvc.perform(post("/api/profile/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(credentials)))
                .andExpect(status().isUnauthorized());

        log.info("Login failure with wrong password passed");
    }

    // Test case: check BNPL eligibility for a valid user
    @Test
    public void testBnplEligibility_True() throws Exception {
        log.info("Testing BNPL eligibility check");

        when(userProfileRepository.findById(1L)).thenReturn(Optional.of(mockUser));

        mockMvc.perform(get("/api/profile/1/bnpl-eligibility"))
                .andExpect(status().isOk());

        log.info("BNPL eligibility check passed");
    }

    // Test case: update password for a valid user
    @Test
    public void testUpdatePassword_Valid() throws Exception {
        log.info("Testing update password - valid case");

        when(userProfileRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(userProfileRepository.existsByPassword("New@Pass123")).thenReturn(false);

        UserProfile updateRequest = new UserProfile();
        updateRequest.setPassword("New@Pass123");

        mockMvc.perform(put("/api/profile/1/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk());

        log.info("Password update test passed");
    }
}