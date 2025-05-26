package com.zeta.backend.repository;

import com.zeta.backend.model.UserProfile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserProfileRepositoryTest {

    private static final Logger logger = LoggerFactory.getLogger(UserProfileRepositoryTest.class);

    @Autowired
    private UserProfileRepository userProfileRepository;

    private UserProfile savedUser;

    /**
     * Setup method to insert a sample UserProfile into the in-memory database
     * before each test runs. This user will be used for testing repository methods.
     */
    @BeforeEach
    void setUp() {
        logger.info("Setting up test user profile in the database");

        UserProfile user = UserProfile.builder()
                .fullName("Ashritha")
                .email("ashritha@example.com")
                .phone("9876543210")
                .address("Hyderabad")
                .annualIncome(450000.0)
                .password("Test@123")                  // Required non-null field in DB
                .isEligibleForBNPL(true)
                .createdAt(LocalDateTime.now())        // Timestamp for creation
                .updatedAt(LocalDateTime.now())        // Timestamp for last update
                .build();

        savedUser = userProfileRepository.save(user);
        logger.info("Test user profile saved with ID: {}", savedUser.getUserId());
    }

    @Test
    @DisplayName("findByEmail: returns UserProfile if email exists")
    void testFindByEmailExists() {
        logger.info("Testing findByEmail with existing email");

        Optional<UserProfile> result = userProfileRepository.findByEmail("ashritha@example.com");
        assertTrue(result.isPresent(), "UserProfile should be found");
        assertEquals("Ashritha", result.get().getFullName(), "Full name should match");
    }

    @Test
    @DisplayName("findByEmail: returns empty if email does not exist")
    void testFindByEmailNotExists() {
        logger.info("Testing findByEmail with non-existing email");

        Optional<UserProfile> result = userProfileRepository.findByEmail("unknown@example.com");
        assertFalse(result.isPresent(), "UserProfile should not be found");
    }

    @Test
    @DisplayName("existsByEmail: returns true if email exists")
    void testExistsByEmailTrue() {
        logger.info("Testing existsByEmail with existing email");

        assertTrue(userProfileRepository.existsByEmail("ashritha@example.com"), "Email should exist");
    }

    @Test
    @DisplayName("existsByEmail: returns false if email does not exist")
    void testExistsByEmailFalse() {
        logger.info("Testing existsByEmail with non-existing email");

        assertFalse(userProfileRepository.existsByEmail("noone@example.com"), "Email should not exist");
    }

    @Test
    @DisplayName("existsByPhone: returns true if phone exists")
    void testExistsByPhoneTrue() {
        logger.info("Testing existsByPhone with existing phone");

        assertTrue(userProfileRepository.existsByPhone("9876543210"), "Phone should exist");
    }

    @Test
    @DisplayName("existsByPhone: returns false if phone does not exist")
    void testExistsByPhoneFalse() {
        logger.info("Testing existsByPhone with non-existing phone");

        assertFalse(userProfileRepository.existsByPhone("0000000000"), "Phone should not exist");
    }

    @Test
    @DisplayName("existsByFullName: returns true if full name exists")
    void testExistsByFullNameTrue() {
        logger.info("Testing existsByFullName with existing full name");

        assertTrue(userProfileRepository.existsByFullName("Ashritha"), "Full name should exist");
    }

    @Test
    @DisplayName("existsByFullName: returns false if full name does not exist")
    void testExistsByFullNameFalse() {
        logger.info("Testing existsByFullName with non-existing full name");

        assertFalse(userProfileRepository.existsByFullName("Unknown"), "Full name should not exist");
    }
}
