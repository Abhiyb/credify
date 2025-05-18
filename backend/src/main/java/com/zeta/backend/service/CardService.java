package com.zeta.backend.service;

import com.zeta.backend.model.Card;
import com.zeta.backend.repository.CardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CardService {

    @Autowired
    private CardRepository cardRepository;

    public List<Card> getCardsByUserId(Long userId) {
        return cardRepository.findByUserId(userId);
    }

    public Optional<Card> getCardById(Long cardId) {
        return cardRepository.findById(cardId);
    }

    public Card saveCard(Card card) {
        return cardRepository.save(card);
    }

    public Card updateCardStatus(Long cardId, String status) {
        Optional<Card> cardOpt = cardRepository.findById(cardId);
        if (cardOpt.isPresent()) {
            Card card = cardOpt.get();
            card.setStatus(status);
            return cardRepository.save(card);
        }
        throw new RuntimeException("Card not found with id " + cardId);
    }
}
