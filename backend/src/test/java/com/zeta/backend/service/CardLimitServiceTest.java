package com.zeta.backend.service;

import com.zeta.backend.exception.CardNotFoundException;
import com.zeta.backend.exception.InvalidLimitUpdateException;
import com.zeta.backend.exception.UserNotFoundException;
import com.zeta.backend.model.Card;
import com.zeta.backend.model.UserProfile;
import com.zeta.backend.repository.CardRepository;
import com.zeta.backend.repository.TransactionRepository;
import com.zeta.backend.repository.UserProfileRepository;
import com.zeta.backend.util.CardApprovalUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class CardLimitServiceTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private UserProfileRepository userProfileRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private CardLimitService cardLimitService;

    private AutoCloseable closeable;
    private Card card;
    private UserProfile userProfile;

    @BeforeEach
    public void setUp() {
        closeable = MockitoAnnotations.openMocks(this);

        userProfile = UserProfile.builder()
                .userId(1L)
                .fullName("Aravind")
                .email("aravind2072@example.com")
                .phone("9385359808")
                .address("123 Main St")
                .annualIncome(500000.0)
                .isEligibleForBNPL(true)
                .build();

        card = Card.builder()
                .cardId(1L)
                .cardType("Visa")
                .creditLimit(100000.0)
                .user(userProfile)
                .build();
    }

    @Test
    void shouldReturnMessageWhenLimitUnchanged() {
        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
        when(userProfileRepository.findById(1L)).thenReturn(Optional.of(userProfile));

        String result = cardLimitService.updateCreditLimit(1L,100000.0);
        assertEquals("No change in credit limit",result);
    }

    @Test
    void shouldDecreaseLimitWhenValid() {
        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
        when(userProfileRepository.findById(1L)).thenReturn(Optional.of(userProfile));

        String result = cardLimitService.updateCreditLimit(1L, 80000.0);

        assertEquals("Credit limit decreased", result);
        verify(cardRepository).save(card);
    }

    @Test
    void shouldIncreaseLimitWhenApproved() {
        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
        when(userProfileRepository.findById(1L)).thenReturn(Optional.of(userProfile));
        when(transactionRepository.countByCardId(1L)).thenReturn(1L);
        mockStatic(CardApprovalUtil.class).when(() ->
                CardApprovalUtil.determineApplicationStatus(userProfile, "Visa", 150000.0)
        ).thenReturn("APPROVED");

        String result = cardLimitService.updateCreditLimit(1L, 150000.0);

        assertEquals("Credit limit increased", result);
        verify(cardRepository).save(card);
    }

    @Test
    void shouldThrowExceptionForCardNotFound() {
        when(cardRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(CardNotFoundException.class, () ->
                cardLimitService.updateCreditLimit(1L, 80000.0));
    }

    @Test
    void shouldThrowExceptionForUserNotFound() {
        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
        when(userProfileRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () ->
                cardLimitService.updateCreditLimit(1L, 80000.0));
    }

    @Test
    void shouldThrowForLowLimitDecrease() {
        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
        when(userProfileRepository.findById(1L)).thenReturn(Optional.of(userProfile));
        mockStatic(CardApprovalUtil.class).when(() ->
                CardApprovalUtil.getMinLimitForCardAndIncome("Visa", 500000.0)
        ).thenReturn(90000.0);

        assertThrows(InvalidLimitUpdateException.class, () ->
                cardLimitService.updateCreditLimit(1L, 80000.0));
    }

}