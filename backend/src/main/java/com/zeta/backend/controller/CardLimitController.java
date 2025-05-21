package com.zeta.backend.controller;

import com.zeta.backend.service.CardLimitService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cards")
public class CardLimitController {

    private final CardLimitService cardLimitService;

    @PutMapping("/{cardId}/limit")
    public ResponseEntity<String> updateCreditLimit(@PathVariable Long cardId, @RequestParam Double newLimit) {
        String response = cardLimitService.updateCreditLimit(cardId, newLimit);
        return ResponseEntity.ok(response);
    }
}
