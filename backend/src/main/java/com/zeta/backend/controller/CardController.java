package com.zeta.backend.controller;

import com.zeta.backend.model.Card;
import com.zeta.backend.service.ICardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cards")
@RequiredArgsConstructor
@CrossOrigin("http://localhost:5173/")
public class CardController {

    private final ICardService cardService;

    @GetMapping("/{userId}")
    public List<Card> getCardByUserId(@PathVariable Long userId) {
        return cardService.getCardDetailsByUserId(userId);
    }
    @PutMapping("/{cardId}/status")
    public  Card putCardByUserId(@PathVariable Long cardId,@RequestParam String status)
    {
        return cardService.putCardByUserId(cardId,status);
    }

}