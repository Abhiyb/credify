package com.zeta.backend.service;

import com.zeta.backend.model.Card;

public interface ICardService {
    Card getCardDetailsByUserId(Long userId);

    Card putCardByUserId(Long cardId, String status);
}
