package com.zeta.backend.service;

import com.zeta.backend.dto.CardDTO;
import com.zeta.backend.exception.CardNotFoundException;
import com.zeta.backend.exception.InvalidCardLimitException;
import com.zeta.backend.model.Card;
import com.zeta.backend.repository.CardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CardServiceImpl implements ICardService {

    private final CardRepository cardRepository;

    /**
     * Fetches all card details associated with a given user ID.
     * Converts each Card entity into a DTO before returning.
     */
    @Override
    public List<CardDTO> getCardDetailsByUserId(Long userId) {
        List<Card> cards = cardRepository.findByApplicationUserUserId(userId);
        return cards.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    /**
     * Updates the status of a card using its ID.
     * Throws an exception if the card does not exist.
     */
    @Override
    public CardDTO putCardByUserId(Long cardId, String status) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new CardNotFoundException("Card not found with ID: " + cardId));

        // Normalize the status value (e.g., "blocked" -> "BLOCKED")
        card.setStatus(status.toUpperCase());

        Card updatedCard = cardRepository.save(card);
        return mapToDto(updatedCard);
    }

    /**
     * Updates the credit limit of the card and adjusts the available limit accordingly.
     * Prevents setting a non-positive credit limit.
     */
    @Override
    public CardDTO updateCardLimit(Long cardId, Double newLimit) {
        if (newLimit <= 0) {
            throw new InvalidCardLimitException("New limit must be greater than 0");
        }

        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new CardNotFoundException("Card not found with ID: " + cardId));

        // Adjust the available limit based on the new credit limit
        double oldLimit = card.getCreditLimit();
        card.setCreditLimit(newLimit);
        card.setAvailableLimit(card.getAvailableLimit() + (newLimit - oldLimit));

        Card updatedCard = cardRepository.save(card);
        return mapToDto(updatedCard);
    }

    /**
     * Maps a Card entity to its corresponding DTO.
     * Includes basic null-check for card holder name.
     */
    private CardDTO mapToDto(Card card) {
        return CardDTO.builder()
                .cardId(card.getCardId())
                .cardNumber(card.getCardNumber())
                .cardType(card.getCardType())
                .status(card.getStatus())
                .creditLimit(card.getCreditLimit())
                .availableLimit(card.getAvailableLimit())
                .expiryDate(card.getExpiryDate())
                .cardHolderName(card.getUser() != null ? card.getUser().getFullName() : "Unknown")
                .build();
    }
}
