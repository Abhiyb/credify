package com.zeta.backend.service;

import com.zeta.backend.exception.UserNotFoundException;
import com.zeta.backend.model.*;
import com.zeta.backend.repository.*;
import com.zeta.backend.util.CardApprovalUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j // Enables logging using SLF4J
public class CardApplicationService implements ICardApplicationService {

    @Autowired
    private CardApplicationRepository applicationRepository;

    @Autowired
    private UserProfileRepository userRepository;

    @Autowired
    private CardRepository cardRepository;

    @Override
    public CardApplication apply(CardApplication application) {
        Long userId = application.getUser().getUserId();
        log.info("Processing credit card application for user ID: {}", userId);

        // Fetch user from repository
        UserProfile user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("User not found with ID: {}", userId);
                    return new RuntimeException("User not found with ID " + userId);
                });

        // Set current date and determine application status based on utility logic
        application.setApplicationDate(LocalDate.now());
        String status = CardApprovalUtil.determineApplicationStatus(user, application.getCardType(), application.getRequestedLimit());
        application.setStatus(status);
        log.info("Application status determined: {}", status);

        // Save application record in repository
        CardApplication savedApplication = applicationRepository.save(application);
        log.info("Application saved with ID: {}", savedApplication.getId());

        // If approved, generate and issue a new credit card
        if ("APPROVED".equalsIgnoreCase(status)) {
            Card creditCard = Card.builder()
                    .cardNumber(generateMaskedCardNumber())
                    .cardType(savedApplication.getCardType())
                    .status("ACTIVE")
                    .creditLimit(savedApplication.getRequestedLimit())
                    .availableLimit(savedApplication.getRequestedLimit())
                    .expiryDate(LocalDate.now().plusYears(5))
                    .application(savedApplication)
                    .user(user)
                    .build();

            cardRepository.save(creditCard);
            log.info("Credit card issued for application ID: {}", savedApplication.getId());
        }

        return savedApplication;
    }

    @Override
    public List<CardApplication> getApplicationsByUserId(Long userId) {
        log.info("Fetching all applications for user ID: {}", userId);

        // Filter applications by user ID
        List<CardApplication> applications = applicationRepository.findAll().stream()
                .filter(app -> app.getUser().getUserId().equals(userId))
                .toList();

        if (applications.isEmpty()) {
            log.warn("No applications found for user ID: {}", userId);
            throw new UserNotFoundException("Card application not found for user ID: " + userId);
        }

        log.info("Found {} application(s) for user ID: {}", applications.size(), userId);
        return applications;
    }

    // Utility method to generate a masked credit card number (last 4 digits visible)
    private String generateMaskedCardNumber() {
        int randomDigits = (int) (Math.random() * 9000 + 1000);
        return "XXXX-XXXX-XXXX-" + randomDigits;
    }
}
