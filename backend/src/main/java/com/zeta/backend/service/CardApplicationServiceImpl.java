package com.zeta.backend.service;

import com.zeta.backend.exception.UserNotFoundException;
import com.zeta.backend.model.*;
import com.zeta.backend.repository.*;
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

    @Autowired
    private CardApplicationRepository applicationRepository;

    @Autowired
    private UserProfileRepository userRepository;

    @Autowired
    private CardRepository cardRepository;

    @Override
    public CardApplication apply(CardApplication application) {
        Long userId = application.getUser().getUserId();
//
//        boolean alreadyApplied = applicationRepository.findAll().stream()
//                .anyMatch(app -> app.getUser().getUserId().equals(userId));
//
//        if (alreadyApplied) {
//            throw new RuntimeException("User with ID " + userId + " has already applied for a credit card.");
//        }

        // Fetch user
        UserProfile user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID " + userId));

        // Set application date and determine status
        application.setApplicationDate(LocalDate.now());
        String status = CardApprovalUtil.determineApplicationStatus(user, application.getCardType(), application.getRequestedLimit());
        application.setStatus(status);

        // Save application
        CardApplication savedApplication = applicationRepository.save(application);

        // If approved, create credit card
        if ("APPROVED".equalsIgnoreCase(status)) {
            Card creditCard = Card.builder()
                    .cardNumber(generateMaskedCardNumber())
                    .cardType(savedApplication.getCardType())
                    .status("ACTIVE")
                    .creditLimit(savedApplication.getRequestedLimit())
                    .availableLimit(savedApplication.getRequestedLimit())
                    .expiryDate(LocalDate.now().plusYears(5))
                    .application(savedApplication)
                    .build();

            cardRepository.save(creditCard);
        }

        return savedApplication;
    }
    @Override
    public List<CardApplication> getApplicationsByUserId(Long userId) {
        List<CardApplication> applications = applicationRepository.findAll().stream()
                .filter(app -> app.getUser().getUserId().equals(userId))
                .toList();

        if (applications.isEmpty()) {
            throw new UserNotFoundException("Card application not found for user ID: " + userId);
        }

        return applications;
    }

    private String generateMaskedCardNumber() {
        int randomDigits = (int) (Math.random() * 9000 + 1000);
        return "XXXX-XXXX-XXXX-" + randomDigits;
    }
}
