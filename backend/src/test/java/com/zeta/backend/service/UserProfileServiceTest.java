package com.zeta.backend.service;

import com.zeta.backend.model.UserProfile;
import com.zeta.backend.repository.UserProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import lombok.extern.slf4j.Slf4j;

@ExtendWith(MockitoExtension.class)
@Slf4j // Lombok annotation to auto-generate SLF4J logger instance as 'log'
public class UserProfileServiceTest {

    @InjectMocks
    private UserProfileService userProfileService; // Service under test with injected mocks

    @Mock
    private UserProfileRepository userProfileRepository; // Mocked repository for database operations

    private UserProfile sampleUser; // Sample user profile for testing

    // Setting up test data before each test
    @BeforeEach
    public void setup() {
        log.info("Setting up test data for UserProfileServiceImplTest");
        sampleUser = new UserProfile();
        sampleUser.setUserId(1L);
        sampleUser.setFullName("John Doe");
        sampleUser.setEmail("john.doe@example.com");
        sampleUser.setPhone("1234567890");
        sampleUser.setAddress("123 Main St");
        sampleUser.setAnnualIncome(500000.0);
        sampleUser.setPassword("Password@123");
        sampleUser.setIsEligibleForBNPL(true);
        log.debug("Sample user created: {}", sampleUser);
    }

    // ----------- createProfile() -----------
    // Testing successful creation of a user profile
    @Test
    public void testCreateProfile_Success() {
        log.info("Testing createProfile success scenario for email: {}", sampleUser.getEmail());

        // Mocking the repository save operation to return the sample user
        when(userProfileRepository.save(sampleUser)).thenReturn(sampleUser);

        // Calling the service method to create the profile
        UserProfile created = userProfileService.createProfile(sampleUser);

        // Verifying the result
        log.debug("Created user profile with email: {}", created.getEmail());
        assertNotNull(created);
        assertEquals(sampleUser.getEmail(), created.getEmail());
        verify(userProfileRepository, times(1)).save(sampleUser);
        log.info("createProfile success test passed for email: {}", created.getEmail());
    }

    // ----------- getProfile() -----------
    // Testing retrieval of an existing user profile by ID
    @Test
    public void testGetProfile_Found() {
        log.info("Testing getProfile success scenario for userId: {}", 1L);

        // Mocking the repository to return the sample user
        when(userProfileRepository.findById(1L)).thenReturn(Optional.of(sampleUser));

        // Calling the service method to get the profile
        Optional<UserProfile> result = userProfileService.getProfile(1L);

        // Verifying the result
        log.debug("Retrieved user profile for userId 1L: {}", result.orElse(null));
        assertTrue(result.isPresent());
        assertEquals("John Doe", result.get().getFullName());
        verify(userProfileRepository, times(1)).findById(1L);
        log.info("getProfile success test passed for userId: {}", 1L);
    }

    // Testing retrieval of a non-existent user profile by ID
    @Test
    public void testGetProfile_NotFound() {
        log.info("Testing getProfile not found scenario for userId: {}", 2L);

        // Mocking the repository to return an empty Optional
        when(userProfileRepository.findById(2L)).thenReturn(Optional.empty());

        // Calling the service method to get the profile
        Optional<UserProfile> result = userProfileService.getProfile(2L);

        // Verifying the result
        log.debug("Result of getProfile for userId 2L: {}", result);
        assertFalse(result.isPresent());
        verify(userProfileRepository, times(1)).findById(2L);
        log.info("getProfile not found test passed for userId: {}", 2L);
    }

    // ----------- updateProfile() -----------
    // Testing successful update of an existing user profile
    @Test
    public void testUpdateProfile_Success() {
        log.info("Testing updateProfile success scenario for userId: {}", 1L);

        // Creating updated user profile data
        UserProfile updatedInfo = new UserProfile();
        updatedInfo.setFullName("Jane Doe");
        updatedInfo.setEmail("jane.doe@example.com");
        updatedInfo.setPhone("0987654321");
        updatedInfo.setAddress("456 Another St");
        updatedInfo.setAnnualIncome(600000.0);
        updatedInfo.setIsEligibleForBNPL(false);
        updatedInfo.setPassword("NewPassword@123");
        log.debug("Updated profile info: {}", updatedInfo);

        // Mocking repository operations
        when(userProfileRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
        when(userProfileRepository.save(any(UserProfile.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Calling the service method to update the profile
        UserProfile updated = userProfileService.updateProfile(1L, updatedInfo);

        // Verifying the updated profile
        log.debug("Profile updated with new details for userId 1L: {}", updated);
        assertEquals("Jane Doe", updated.getFullName());
        assertEquals("jane.doe@example.com", updated.getEmail());
        assertEquals("0987654321", updated.getPhone());
        assertEquals("456 Another St", updated.getAddress());
        assertEquals(600000.0, updated.getAnnualIncome());
        assertFalse(updated.getIsEligibleForBNPL());
        assertEquals("NewPassword@123", updated.getPassword());

        // Verifying repository interactions
        verify(userProfileRepository, times(1)).findById(1L);
        verify(userProfileRepository, times(1)).save(any(UserProfile.class));
        log.info("updateProfile success test passed for userId: {}", 1L);
    }

    // Testing update of a non-existent user profile (should throw exception)
    @Test
    public void testUpdateProfile_UserNotFound_ThrowsException() {
        log.info("Testing updateProfile not found scenario for userId: {}", 99L);

        // Creating updated user profile data
        UserProfile updatedInfo = new UserProfile();
        log.debug("Updated profile info: {}", updatedInfo);

        // Mocking the repository to return an empty Optional
        when(userProfileRepository.findById(99L)).thenReturn(Optional.empty());

        // Calling the service method and expecting an exception
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userProfileService.updateProfile(99L, updatedInfo);
        });

        // Verifying the exception message
        log.debug("Exception thrown: {}", exception.getMessage());
        assertEquals("User profile not found", exception.getMessage());
        verify(userProfileRepository, times(1)).findById(99L);
        verify(userProfileRepository, never()).save(any());
        log.info("updateProfile not found test passed for userId: {}", 99L);
    }

    // ----------- updatePassword() -----------
    // Testing successful password update for an existing user
    @Test
    public void testUpdatePassword_Success() {
        log.info("Testing updatePassword success scenario for userId: {}", 1L);

        String newPassword = "SecurePass@123";
        log.debug("New password: {}", newPassword);

        // Mocking repository operations
        when(userProfileRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
        when(userProfileRepository.save(any(UserProfile.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Calling the service method to update the password
        UserProfile updated = userProfileService.updatePassword(1L, newPassword);

        // Verifying the updated password
        log.debug("Password updated successfully for userId 1L: {}", updated);
        assertEquals(newPassword, updated.getPassword());
        verify(userProfileRepository, times(1)).findById(1L);
        verify(userProfileRepository, times(1)).save(any(UserProfile.class));
        log.info("updatePassword success test passed for userId: {}", 1L);
    }

    // Testing password update for a non-existent user (should throw exception)
    @Test
    public void testUpdatePassword_UserNotFound_ThrowsException() {
        log.info("Testing updatePassword not found scenario for userId: {}", 2L);

        String newPassword = "NewPass123";
        log.debug("New password: {}", newPassword);

        // Mocking the repository to return an empty Optional
        when(userProfileRepository.findById(2L)).thenReturn(Optional.empty());

        // Calling the service method and expecting an exception
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userProfileService.updatePassword(2L, newPassword);
        });

        // Verifying the exception message
        log.debug("Exception thrown: {}", exception.getMessage());
        assertEquals("User not found", exception.getMessage());
        verify(userProfileRepository, times(1)).findById(2L);
        verify(userProfileRepository, never()).save(any());
        log.info("updatePassword not found test passed for userId: {}", 2L);
    }

    // ----------- Neutral case: Update password with same password -----------
    // Testing password update with the same password (neutral case)
    @Test
    public void testUpdatePassword_SamePassword() {
        log.info("Testing updatePassword with same password scenario for userId: {}", 1L);

        String currentPassword = sampleUser.getPassword();
        log.debug("Current password (used for update): {}", currentPassword);

        // Mocking repository operations
        when(userProfileRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
        when(userProfileRepository.save(any(UserProfile.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Calling the service method to update the password with the same value
        UserProfile updated = userProfileService.updatePassword(1L, currentPassword);

        // Verifying the password remains unchanged
        log.debug("Password updated with same value for userId 1L: {}", updated);
        assertEquals(currentPassword, updated.getPassword());
        verify(userProfileRepository, times(1)).findById(1L);
        verify(userProfileRepository, times(1)).save(any(UserProfile.class));
        log.info("updatePassword with same password test passed for userId: {}", 1L);
    }
}