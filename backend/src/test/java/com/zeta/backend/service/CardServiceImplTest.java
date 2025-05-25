package com.zeta.backend.service;

import com.zeta.backend.dto.CardDTO;
import com.zeta.backend.exception.CardNotFoundException;
import com.zeta.backend.model.Card;
import com.zeta.backend.model.UserProfile;
import com.zeta.backend.repository.CardRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

/**
 * Unit tests for CardServiceImpl methods using Mockito.
 */
class CardServiceImplTest {

    @Mock
    private CardRepository cardRepository;

    @InjectMocks
    private CardServiceImpl cardService;

    public CardServiceImplTest() {
        // Initialize mocks before running tests
        openMocks(this);
    }

    /**
     * Utility method to build a dummy Card object for testing.
     */
    private Card mockCard() {
        Card card = new Card();
        card.setCardId(1L);
        card.setCardNumber("1234567890123456");
        card.setCardType("VISA");
        card.setStatus("ACTIVE");
        card.setCreditLimit(10000.0);
        card.setAvailableLimit(8000.0);
        card.setExpiryDate(LocalDate.now().plusYears(2));
        UserProfile user = new UserProfile();
        user.setFullName("John Doe");
        card.setUser(user);
        return card;
    }

    /**
     * Test whether the service correctly fetches card details by user ID.
     */
    @Test
    void testGetCardDetailsByUserId() {
        when(cardRepository.findByApplicationUserUserId(1L))
                .thenReturn(List.of(mockCard()));

        List<CardDTO> result = cardService.getCardDetailsByUserId(1L);

        // Should return one card
        assertEquals(1, result.size());

        // Verify the card holder name is mapped correctly
        assertEquals("John Doe", result.get(0).getCardHolderName());
    }

    /**
     * Test updating card status when the card exists.
     */
    @Test
    void testPutCardByUserId() {
        Card card = mockCard();

        // Mock card fetch and save
        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
        when(cardRepository.save(any(Card.class))).thenReturn(card);

        // Call the service to update status
        CardDTO updated = cardService.putCardByUserId(1L, "BLOCKED");

        // Confirm the updated status is reflected in the DTO
        assertEquals("BLOCKED", updated.getStatus());
    }

    /**
     * Test handling of a case where card ID doesn't exist.
     * Should throw a CardNotFoundException.
     */
    @Test
    void testPutCardByUserId_NotFound() {
        // Card not found in DB
        when(cardRepository.findById(1L)).thenReturn(Optional.empty());

        // Expect an exception when trying to update non-existent card
        assertThrows(CardNotFoundException.class, () -> {
            cardService.putCardByUserId(1L, "ACTIVE");
        });
    }
}
