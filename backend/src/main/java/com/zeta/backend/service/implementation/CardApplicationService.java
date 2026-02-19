package com.zeta.backend.service.implementation;

import com.zeta.backend.exception.CardApplicationNotFoundException;
import com.zeta.backend.exception.DuplicateCardApplicationException;
import com.zeta.backend.model.Card;
import com.zeta.backend.model.CardApplication;
import com.zeta.backend.model.UserProfile;
import com.zeta.backend.repository.CardApplicationRepository;
import com.zeta.backend.repository.CardRepository;
import com.zeta.backend.repository.UserProfileRepository;
import com.zeta.backend.service.ICardApplicationService;
import com.zeta.backend.util.CardApprovalUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CardApplicationService implements ICardApplicationService {

    private final CardApplicationRepository applicationRepository;
    private final UserProfileRepository userRepository;
    private final CardRepository cardRepository;

    @Override
    public CardApplication apply(String email, CardApplication application) {

        log.info("Processing credit card application for user email: {}", email);

        UserProfile user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        boolean alreadyExists = applicationRepository.existsByUserUserIdAndCardTypeAndRequestedLimit(
                user.getUserId(),
                application.getCardType(),
                application.getRequestedLimit()
        );

        if (alreadyExists) {
            log.warn("Duplicate application attempt by user {}", email);
            throw new DuplicateCardApplicationException("Application already exists for same card type and limit.");
        }

        application.setUser(user);
        application.setApplicationDate(LocalDate.now());

        String status = CardApprovalUtil.determineApplicationStatus(
                user,
                application.getCardType(),
                application.getRequestedLimit()
        );

        application.setStatus(status);

        CardApplication savedApplication = applicationRepository.save(application);

        log.info("Application saved with ID: {}", savedApplication.getId());

        if ("APPROVED".equalsIgnoreCase(status)) {

            Card creditCard = Card.builder()
                    .cardNumber(generateValidCardNumber(savedApplication.getCardType()))
                    .cvv(generateCVV(savedApplication.getCardType()))
                    .cardType(savedApplication.getCardType())
                    .status("ACTIVE")
                    .creditLimit(savedApplication.getRequestedLimit())
                    .availableLimit(savedApplication.getRequestedLimit())
                    .expiryDate(LocalDate.now().plusYears(5))
                    .application(savedApplication)
                    .user(user)
                    .build();

            // NEW: Automatically set expiry_month and expiry_year from expiryDate
            LocalDate expDate = creditCard.getExpiryDate();
            if (expDate != null) {
                int month = expDate.getMonthValue();
                creditCard.setExpiryMonth(month < 10 ? "0" + month : String.valueOf(month));  // e.g. "02"

                int year = expDate.getYear() % 100;  // last 2 digits, e.g. 31 for 2031
                creditCard.setExpiryYear(String.valueOf(year));
            }

            cardRepository.save(creditCard);

            log.info("Credit card issued for application ID: {}", savedApplication.getId());
        }

        return savedApplication;
    }

    @Override
    public List<CardApplication> getApplicationsByEmail(String email) {

        log.info("Fetching all applications for user email: {}", email);

        UserProfile user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        List<CardApplication> applications = applicationRepository.findByUserUserId(user.getUserId());

        return applications; // ✅ return empty list if none found
    }


    @Override
    public CardApplication getApplicationById(String email, Long applicationId) {

        log.info("Fetching application {} for user {}", applicationId, email);

        UserProfile user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        CardApplication application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new CardApplicationNotFoundException("Application not found with ID: " + applicationId));

        // SECURITY CHECK (very important)
        if (!application.getUser().getUserId().equals(user.getUserId())) {
            throw new RuntimeException("You are not allowed to access this application");
        }

        return application;
    }

    @Override
    public CardApplication updateApplication(String email, Long applicationId, CardApplication updatedApplication) {

        log.info("Updating application {} for user {}", applicationId, email);

        CardApplication existing = getApplicationById(email, applicationId);

        existing.setCardType(updatedApplication.getCardType());
        existing.setRequestedLimit(updatedApplication.getRequestedLimit());
        existing.setStatus(updatedApplication.getStatus());

        return applicationRepository.save(existing);
    }

//    @Override
//    public void deleteApplication(String email, Long applicationId) {
//
//        log.info("Deleting application {} for user {}", applicationId, email);
//
//        CardApplication application = getApplicationById(email, applicationId);
//
//        applicationRepository.delete(application);
//    }

    private String generateMaskedCardNumber() {
        int randomDigits = (int) (Math.random() * 9000 + 1000);
        return "XXXX-XXXX-XXXX-" + randomDigits;
    }
    // Generate valid card number with Luhn check digit
    private String generateValidCardNumber(String cardType) {
        String prefix;
        int length;

        switch (cardType.toUpperCase()) {
            case "VISA":
                prefix = "4";
                length = 16;
                break;
            case "MASTERCARD":
                prefix = "5";
                length = 16;
                break;
            case "AMEX":
                prefix = (Math.random() < 0.5) ? "34" : "37";
                length = 15;
                break;
            default:
                prefix = "4";
                length = 16;
                break;
        }

        // Generate random digits for the rest (except last digit)
        StringBuilder sb = new StringBuilder(prefix);
        for (int i = prefix.length(); i < length - 1; i++) {
            sb.append((int) (Math.random() * 10)); // 0-9
        }

        // Calculate Luhn check digit
        String base = sb.toString();
        int sum = 0;
        boolean alternate = false;
        for (int i = base.length() - 1; i >= 0; i--) {
            int digit = Character.getNumericValue(base.charAt(i));
            if (alternate) {
                digit *= 2;
                if (digit > 9) digit -= 9;
            }
            sum += digit;
            alternate = !alternate;
        }
        int checkDigit = (10 - (sum % 10)) % 10;
        sb.append(checkDigit);

        return sb.toString();
    }

    // Generate CVV
    private String generateCVV(String cardType) {
        if ("AMEX".equalsIgnoreCase(cardType)) {
            return String.format("%04d", (int) (Math.random() * 10000)); // 4 digits
        } else {
            return String.format("%03d", (int) (Math.random() * 1000)); // 3 digits
        }
    }
}
