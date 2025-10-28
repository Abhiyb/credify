package com.zeta.backend.controller;

import com.zeta.backend.exception.CardNotFoundException;
import com.zeta.backend.exception.InvalidLimitUpdateException;
import com.zeta.backend.exception.UserNotFoundException;
import com.zeta.backend.service.CardLimitService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cards")
public class CardLimitController {

    private final CardLimitService cardLimitService;

//    @PutMapping("/{cardId}/limit")
//    public ResponseEntity<String> updateCreditLimit(@PathVariable Long cardId, @RequestParam Double newLimit) {
//        try {
//            String response = cardLimitService.updateCreditLimit(cardId, newLimit);
//            return ResponseEntity.ok(response);
//        } catch (CardNotFoundException | UserNotFoundException e) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
//        } catch (InvalidLimitUpdateException e) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body("An unexpected error occurred: " + e.getMessage());
//        }
//    }
}