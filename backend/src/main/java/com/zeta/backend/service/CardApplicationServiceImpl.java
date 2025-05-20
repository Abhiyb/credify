package com.zeta.backend.service;

import com.zeta.backend.exception.UserNotFoundException;
import com.zeta.backend.model.*;
import com.zeta.backend.repository.*;
import com.zeta.backend.service.ICardApplicationService;
import com.zeta.backend.util.CardApprovalUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CardApplicationServiceImpl implements ICardApplicationService {

    @Autowired private CardApplicationRepository applicationRepository;
    @Autowired private UserProfileRepository userRepository;


    @Override
    public CardApplication apply(CardApplication application) {
        Long userId = application.getUser().getUserId();
        Optional<CardApplication> existing = applicationRepository.findById(userId);
        if (existing.isPresent()) {
            throw new RuntimeException("User with ID " + userId + " has already applied for a credit card.");
        }

        UserProfile user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID " + userId));

        application.setApplicationDate(LocalDate.now());

        // Determine status using utility
        String status = CardApprovalUtil.determineApplicationStatus(user, application.getCardType(), application.getRequestedLimit());
        application.setStatus(status);

        return applicationRepository.save(application);
    }


    @Override
    public CardApplication getApplicationsByUserId(Long userId) {
        return applicationRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Card application not found"));
    }
}
