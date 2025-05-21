package com.zeta.backend.service;

import com.zeta.backend.model.Card;
import com.zeta.backend.repository.CardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CardServiceImpl implements ICardService {

    private final CardRepository cardRepository;

    @Override
    public Card getCardDetailsByUserId(Long userId) {
        return cardRepository.findByApplicationUserUserId(userId);
    }

    @Override
    public Card putCardByUserId(Long cardId, String status) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new RuntimeException("Card not found with ID: " + cardId));

        card.setStatus(status.toUpperCase());
        return cardRepository.save(card);
    }

}
