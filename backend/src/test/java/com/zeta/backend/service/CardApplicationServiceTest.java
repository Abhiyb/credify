package com.zeta.backend.service;

import com.zeta.backend.exception.UserNotFoundException;
import com.zeta.backend.model.*;
import com.zeta.backend.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Slf4j
class CardApplicationServiceTest {

    @InjectMocks
    private CardApplicationService service;

    @Mock
    private CardApplicationRepository applicationRepository;

    @Mock
    private UserProfileRepository userRepository;

    @Mock
    private CardRepository cardRepository;

    private UserProfile mockUser;
    private CardApplication mockApplication;

    /**
     * Sets up a mock user and a mock card application before each test case.
     */
    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        mockUser = UserProfile.builder()
                .userId(1L)
                .fullName("sravani")
                .annualIncome(600000.0)
                .build();

        mockApplication = CardApplication.builder()
                .user(mockUser)
                .cardType("VISA")
                .requestedLimit(20000.0)
                .build();

        log.info("Test setup complete");
    }

    /**
     * ✅ Test applying for a card when the user exists and meets approval criteria.
     * Verifies that the application is saved with "APPROVED" status and a card is issued.
     */
    @Test
    void applyCard_approvedStatus_savesApplicationAndCard() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(applicationRepository.save(any(CardApplication.class))).thenAnswer(i -> {
            CardApplication app = i.getArgument(0);
            app.setId(1L);
            return app;
        });

        CardApplication result = service.apply(mockApplication);

        log.info("Application result: status={}, date={}", result.getStatus(), result.getApplicationDate());
        assertEquals("APPROVED", result.getStatus());
        assertEquals(LocalDate.now(), result.getApplicationDate());
        verify(cardRepository, times(1)).save(any(Card.class));
    }

    /**
     * ✅ Test applying for a card with a non-existent user ID.
     * Expects a RuntimeException to be thrown with a meaningful error message.
     */
    @Test
    void applyCard_userNotFound_throwsException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            service.apply(mockApplication);
        });

        log.warn("Exception thrown as expected: {}", ex.getMessage());
        assertTrue(ex.getMessage().contains("User not found with ID"));
    }

    /**
     * ✅ Test retrieving all card applications for a valid user.
     * Ensures that the returned list contains all applications linked to the user.
     */
    @Test
    void getApplicationsByUserId_existingUser_returnsApplications() {
        CardApplication app1 = CardApplication.builder().user(mockUser).build();
        CardApplication app2 = CardApplication.builder().user(mockUser).build();

        when(applicationRepository.findAll()).thenReturn(List.of(app1, app2));

        List<CardApplication> results = service.getApplicationsByUserId(1L);

        log.info("Found {} applications for user ID {}", results.size(), 1L);
        assertEquals(2, results.size());
    }

    /**
     * ✅ Test retrieving applications for a user when none exist.
     * Expects a UserNotFoundException to be thrown.
     */
    @Test
    void getApplicationsByUserId_noApplications_throwsException() {
        when(applicationRepository.findAll()).thenReturn(Collections.emptyList());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> {
            service.getApplicationsByUserId(1L);
        });

        log.warn("Expected UserNotFoundException: {}", exception.getMessage());
    }



}
