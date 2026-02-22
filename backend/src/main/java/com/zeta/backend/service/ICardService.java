package com.zeta.backend.service;

import com.zeta.backend.dto.CardDTO;

import java.util.List;

public interface ICardService {
    List<CardDTO> getCardDetailsByUserId(Long userId);
    CardDTO putCardByUserId(Long cardId, String status);
    CardDTO updateCardLimit(Long cardId, Double newLimit);
    void validateCardOwnership(Long cardId, String userEmail);
}
