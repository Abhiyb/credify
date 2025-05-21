package com.zeta.backend.service;

import com.zeta.backend.model.Card;

import java.util.List;

public interface ICardService {
    List<Card> getCardDetailsByUserId(Long userId);

    Card putCardByUserId(Long cardId, String status);
}
