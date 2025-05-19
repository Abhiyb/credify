package com.zeta.backend.service;

import static org.junit.jupiter.api.Assertions.*;

import com.zeta.backend.exception.ResourceNotFoundException;
import com.zeta.backend.model.Card;
import com.zeta.backend.repository.CardRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CardServiceTest {

    @Mock
    private CardRepository cardRepository;

    @InjectMocks
    private CardService cardService;

    private Card card;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        card = new Card();
//        card.setCardId(1L);
        card.setCardNumber("123456789012");
//        card.setUserId(10L);
        card.setCardType("VISA");
        card.setStatus("ACTIVE");
    }

    @Test
    public void testGetCardsByUserId_Success() {
        List<Card> cards = Arrays.asList(card);
        when(cardRepository.findByUserId(10L)).thenReturn(cards);

        List<Card> result = cardService.getCardsByUserId(10L);
        assertEquals(1, result.size());
        assertEquals("123456789012", result.get(0).getCardNumber());
        verify(cardRepository, times(1)).findByUserId(10L);
    }

    @Test
    public void testGetCardsByUserId_NoCards() {
        when(cardRepository.findByUserId(99L)).thenReturn(Collections.emptyList());

        List<Card> result = cardService.getCardsByUserId(99L);
        assertTrue(result.isEmpty());
        verify(cardRepository, times(1)).findByUserId(99L);
    }

    @Test
    public void testUpdateCardStatus_Success() {
        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
        when(cardRepository.save(any(Card.class))).thenReturn(card);

        Card updatedCard = cardService.updateCardStatus(1L, "BLOCKED");
        assertEquals("BLOCKED", updatedCard.getStatus());
        verify(cardRepository, times(1)).findById(1L);
        verify(cardRepository, times(1)).save(card);
    }

    @Test
    public void testUpdateCardStatus_CardNotFound() {
        when(cardRepository.findById(2L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> cardService.updateCardStatus(2L, "ACTIVE"));

        assertEquals("Card not found with id 2", ex.getMessage());
        verify(cardRepository, times(1)).findById(2L);
        verify(cardRepository, never()).save(any());
    }

}
