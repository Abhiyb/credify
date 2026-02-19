package com.zeta.backend.service;

import com.zeta.backend.model.CardApplication;

import java.util.List;

public interface ICardApplicationService {

    CardApplication apply(String email, CardApplication application);

    List<CardApplication> getApplicationsByEmail(String email);

    CardApplication getApplicationById(String email, Long applicationId);

    CardApplication updateApplication(String email, Long applicationId, CardApplication updatedApplication);

//    void deleteApplication(String email, Long applicationId);
}
