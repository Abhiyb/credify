package com.zeta.backend.repository;

import com.zeta.backend.model.CardApplication;
import com.zeta.backend.model.UserProfile;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestPropertySource("classpath:application-test.properties")
class CardApplicationRepositoryTest {

    @Autowired
    private CardApplicationRepository cardApplicationRepository;

    @Autowired
    private UserProfileRepository userRepository;

    @Test
    @DisplayName("Should return true when application exists")
    void existsByUserUserIdAndCardTypeAndRequestedLimit_true() {
        UserProfile user = new UserProfile();
        user.setFullName("John");
        user.setEmail("john@example.com");
        user.setPassword("pass123");
        user.setAnnualIncome(500000.0);
        user.setPhone("9876543210");
        user.setAddress("123 Main St, Anytown, NY 10001");
        user = userRepository.save(user);

        CardApplication application = new CardApplication();
        application.setUser(user);
        application.setCardType("VISA");
        application.setRequestedLimit(100000.0);
        application.setStatus("PENDING");
        application.setApplicationDate(LocalDate.now());
        cardApplicationRepository.save(application);

        boolean exists = cardApplicationRepository
                .existsByUserUserIdAndCardTypeAndRequestedLimit(
                        user.getUserId(), "VISA", 100000.0);

        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Should return false when application does not exist")
    void existsByUserUserIdAndCardTypeAndRequestedLimit_false() {
        boolean exists = cardApplicationRepository
                .existsByUserUserIdAndCardTypeAndRequestedLimit(
                        999L, "MASTER", 200000.0);

        assertThat(exists).isFalse();
    }
}
