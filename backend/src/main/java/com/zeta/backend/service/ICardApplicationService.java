package com.zeta.backend.service;

import com.zeta.backend.model.CardApplication;

import java.util.List;

public interface ICardApplicationService {
    CardApplication apply(CardApplication cardApplication);
    List<CardApplication> getApplicationsByUserId(Long userId);
}
