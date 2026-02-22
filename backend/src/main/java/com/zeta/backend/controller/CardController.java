package com.zeta.backend.controller;

import com.zeta.backend.dto.CardDTO;
import com.zeta.backend.exception.CardNotFoundException;
import com.zeta.backend.exception.InvalidCardLimitException;
import com.zeta.backend.repository.UserProfileRepository;
import com.zeta.backend.service.ICardService;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cards")
@RequiredArgsConstructor
@CrossOrigin("http://localhost:3000")
@Slf4j
public class CardController {

    private final ICardService cardService;
    private final UserProfileRepository userRepository;

    // ========================== GET MY CARDS ==========================
    @GetMapping("/me")
    public ResponseEntity<List<CardDTO>> getMyCards(Authentication authentication) {
        String email = authentication.getName();
        log.info("Fetching cards for user: {}", email);

        Long userId = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getUserId();

        List<CardDTO> cards = cardService.getCardDetailsByUserId(userId);
        return ResponseEntity.ok(cards);
    }

    // ========================== UPDATE CARD STATUS ==========================
    @PutMapping("/{cardId}/status")
    public ResponseEntity<CardDTO> updateCardStatus(
            Authentication authentication,
            @PathVariable @Min(1) Long cardId,
            @RequestParam @NotBlank String status) {

        String email = authentication.getName();
        log.info("Updating status of cardId: {} by user: {}", cardId, email);

        cardService.validateCardOwnership(cardId, email);

        CardDTO updatedCard = cardService.putCardByUserId(cardId, status);
        return ResponseEntity.ok(updatedCard);
    }

    // ========================== UPDATE CARD LIMIT ==========================
    @PutMapping("/{cardId}/limit")
    public ResponseEntity<CardDTO> updateCardLimit(
            Authentication authentication,
            @PathVariable @Min(1) Long cardId,
            @RequestParam @Positive Double newLimit) {

        String email = authentication.getName();
        log.info("Updating card limit for cardId: {} by user: {}", cardId, email);

        cardService.validateCardOwnership(cardId, email);

        CardDTO updatedCard = cardService.updateCardLimit(cardId, newLimit);
        return ResponseEntity.ok(updatedCard);
    }

    // ========================== EXCEPTION HANDLERS ==========================
    @ExceptionHandler(CardNotFoundException.class)
    public ResponseEntity<String> handleCardNotFound(CardNotFoundException ex) {
        log.warn("Card not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(InvalidCardLimitException.class)
    public ResponseEntity<String> handleInvalidLimit(InvalidCardLimitException ex) {
        log.warn("Invalid card limit: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneral(Exception ex) {
        log.error("Unexpected error occurred: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("An unexpected error occurred: " + ex.getMessage());
    }
}
