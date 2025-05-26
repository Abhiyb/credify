package com.zeta.backend.service;

import com.zeta.backend.exception.CardApplicationNotFoundException;
import com.zeta.backend.exception.DuplicateCardApplicationException;
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

        // check for duplicate application
        boolean alreadyExists = applicationRepository.existsByUserUserIdAndCardTypeAndRequestedLimit(
                user.getUserId(), application.getCardType(), application.getRequestedLimit());

        if (alreadyExists) {
            log.warn("Duplicate application attempt for user ID: {} with card type: {} and limit: {}",
                    user.getUserId(), application.getCardType(), application.getRequestedLimit());
            throw new DuplicateCardApplicationException("Application already exists for the same card type and limit.");
        }
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

    @Override
    public CardApplication getApplicationById(Long applicationId) {
        log.debug("Fetching application with ID: {}", applicationId);
        return applicationRepository.findById(applicationId)
                .orElseThrow(() -> new CardApplicationNotFoundException("Application not found with ID: " + applicationId));
    }
    @Override
    public CardApplication updateApplication(Long applicationId, CardApplication updatedApplication) {
        CardApplication existing = getApplicationById(applicationId);

        log.debug("Updating application ID: {} with new data", applicationId);

        existing.setCardType(updatedApplication.getCardType());
        existing.setRequestedLimit(updatedApplication.getRequestedLimit());
        existing.setStatus(updatedApplication.getStatus());

        return applicationRepository.save(existing);
    }

    @Override
    public void deleteApplication(Long applicationId) {
        CardApplication application = getApplicationById(applicationId);
        log.debug("Deleting application ID: {}", applicationId);
        applicationRepository.delete(application);
    }

    // Utility method to generate a masked credit card number (last 4 digits visible)
    private String generateMaskedCardNumber() {
        int randomDigits = (int) (Math.random() * 9000 + 1000);
        return "XXXX-XXXX-XXXX-" + randomDigits;
    }
}
