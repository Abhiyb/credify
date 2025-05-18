package com.zeta.backend.controller;

import com.zeta.backend.model.Card;
import com.zeta.backend.service.CardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cards")
public class CardController {

    @Autowired
    private CardService cardService;

    // Get all cards for a user
    @GetMapping("/{userId}")
    public ResponseEntity<List<Card>> getCardsByUserId(@PathVariable Long userId) {
        List<Card> cards = cardService.getCardsByUserId(userId);
        if (cards.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(cards);
    }

    // Activate or Block a card
    @PutMapping("/{cardId}/status")
    public ResponseEntity<Card> updateCardStatus(@PathVariable Long cardId, @RequestParam String status) {
        try {
            Card updatedCard = cardService.updateCardStatus(cardId, status.toUpperCase());
            return ResponseEntity.ok(updatedCard);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Optional: Add a card (if needed)
    @PostMapping
    public ResponseEntity<Card> addCard(@RequestBody Card card) {
        Card savedCard = cardService.saveCard(card);
        return ResponseEntity.ok(savedCard);
    }
}
