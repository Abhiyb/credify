package com.zeta.backend.service;

import com.zeta.backend.model.UserProfile;
import com.zeta.backend.repository.UserProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserProfileServiceTest {

    @Mock
    private UserProfileRepository userProfileRepository;

    @InjectMocks
    private UserProfileServiceImpl userProfileService;

    private UserProfile sampleProfile;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        sampleProfile = new UserProfile();
        sampleProfile.setUserId(1L);
        sampleProfile.setFullName("John Doe");
        sampleProfile.setEmail("john@example.com");
        sampleProfile.setPhone("1234567890");
        sampleProfile.setAddress("123 Main St");
        sampleProfile.setAnnualIncome(50000.0);
        sampleProfile.setIsEligibleForBNPL(true);
    }

    @Test
    void testCreateProfile() {
        when(userProfileRepository.save(any(UserProfile.class))).thenReturn(sampleProfile);

        UserProfile created = userProfileService.createProfile(sampleProfile);

        assertNotNull(created);
        assertEquals("John Doe", created.getFullName());
        verify(userProfileRepository, times(1)).save(sampleProfile);
    }

    @Test
    void testGetProfile_Found() {
        when(userProfileRepository.findById(1L)).thenReturn(Optional.of(sampleProfile));

        Optional<UserProfile> found = userProfileService.getProfile(1L);

        assertTrue(found.isPresent());
        assertEquals("John Doe", found.get().getFullName());
        verify(userProfileRepository, times(1)).findById(1L);
    }

    @Test
    void testGetProfile_NotFound() {
        when(userProfileRepository.findById(2L)).thenReturn(Optional.empty());

        Optional<UserProfile> found = userProfileService.getProfile(2L);

        assertFalse(found.isPresent());
        verify(userProfileRepository, times(1)).findById(2L);
    }

    @Test
    void testUpdateProfile_Success() {
        UserProfile updatedInfo = new UserProfile();
        updatedInfo.setFullName("Jane Doe");
        updatedInfo.setEmail("jane@example.com");
        updatedInfo.setPhone("0987654321");
        updatedInfo.setAddress("456 Elm St");
        updatedInfo.setAnnualIncome(75000.0);
        updatedInfo.setIsEligibleForBNPL(false);

        when(userProfileRepository.findById(1L)).thenReturn(Optional.of(sampleProfile));
        when(userProfileRepository.save(any(UserProfile.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserProfile updated = userProfileService.updateProfile(1L, updatedInfo);

        assertNotNull(updated);
        assertEquals("Jane Doe", updated.getFullName());
        assertEquals("jane@example.com", updated.getEmail());
        assertEquals("0987654321", updated.getPhone());
        assertEquals("456 Elm St", updated.getAddress());
        assertEquals(75000.0, updated.getAnnualIncome());
        assertFalse(updated.getIsEligibleForBNPL());
        verify(userProfileRepository, times(1)).findById(1L);
        verify(userProfileRepository, times(1)).save(any(UserProfile.class));
    }

    @Test
    void testUpdateProfile_NotFound() {
        UserProfile updatedInfo = new UserProfile();
        when(userProfileRepository.findById(3L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userProfileService.updateProfile(3L, updatedInfo);
        });

        assertEquals("User profile not found", exception.getMessage());
        verify(userProfileRepository, times(1)).findById(3L);
        verify(userProfileRepository, never()).save(any(UserProfile.class));
    }
}
