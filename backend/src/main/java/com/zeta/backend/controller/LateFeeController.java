package com.zeta.backend.controller;

import com.zeta.backend.service.LateFeeCalculator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/latefees")
@RequiredArgsConstructor
public class LateFeeController {

    private final LateFeeCalculator lateFeeCalculator;

    @GetMapping("/{cardId}")
    public ResponseEntity<Double> getLateFeeForCard(@PathVariable Long cardId) {
        double fee = lateFeeCalculator.calculateTotalLateFeeByCardId(cardId);
        return ResponseEntity.ok(fee);
    }
}
