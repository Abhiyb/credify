package com.zeta.backend.controller;

import com.zeta.backend.dto.CardDTO;
import com.zeta.backend.exception.CardNotFoundException;
import com.zeta.backend.exception.InvalidCardLimitException;
import com.zeta.backend.service.ICardService;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for managing card-related operations.
 */
@RestController
@RequestMapping("/api/cards")
@RequiredArgsConstructor
@CrossOrigin("http://localhost:5173")
@Slf4j
public class CardController {

    private final ICardService cardService;

    /**
     * Fetch cards by user ID.
     * @param userId the ID of the user
     * @return list of cards associated with the user
     */
    @GetMapping("/{userId}")
    public ResponseEntity<List<CardDTO>> getCardByUserId(@PathVariable @Min(1) Long userId) {
        log.info("Fetching cards for userId: {}", userId);
        List<CardDTO> cards = cardService.getCardDetailsByUserId(userId);
        return new ResponseEntity<>(cards, HttpStatus.OK);
    }

    /**
     * Update the status of a card.
     * @param cardId the ID of the card
     * @param status the new status to be updated
     * @return the updated card
     */
    @PutMapping("/{cardId}/status")
    public ResponseEntity<CardDTO> putCardByUserId(
            @PathVariable @Min(1) Long cardId,
            @RequestParam @NotBlank String status) {
        log.info("Updating status of cardId: {} to {}", cardId, status);
        CardDTO updatedCard = cardService.putCardByUserId(cardId, status);
        return new ResponseEntity<>(updatedCard, HttpStatus.OK);
    }

    /**
     * Update the limit of a card.
     * @param cardId the ID of the card
     * @param newLimit the new limit to be set
     * @return the updated card
     */
    @PutMapping("/{cardId}/limit")
    public ResponseEntity<CardDTO> updateCardLimit(
            @PathVariable @Min(1) Long cardId,
            @RequestParam @Positive Double newLimit) {
        log.info("Updating card limit for cardId: {} to {}", cardId, newLimit);
        CardDTO updatedCard = cardService.updateCardLimit(cardId, newLimit);
        return new ResponseEntity<>(updatedCard, HttpStatus.OK);
    }

    /**
     * Handles when a card is not found.
     */
    @ExceptionHandler(CardNotFoundException.class)
    public ResponseEntity<String> handleCardNotFoundException(CardNotFoundException ex) {
        log.warn("Card not found: {}", ex.getMessage());
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    /**
     * Handles when an invalid card limit is provided.
     */
    @ExceptionHandler(InvalidCardLimitException.class)
    public ResponseEntity<String> handleInvalidCardLimitException(InvalidCardLimitException ex) {
        log.warn("Invalid card limit: {}", ex.getMessage());
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles any other unexpected exceptions.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneralException(Exception ex) {
        log.error("Unexpected error occurred: {}", ex.getMessage(), ex);
        return new ResponseEntity<>("An unexpected error occurred: " + ex.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
