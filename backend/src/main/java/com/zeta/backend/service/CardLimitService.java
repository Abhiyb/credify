package com.zeta.backend.service;

import com.zeta.backend.exception.CardNotFoundException;
import com.zeta.backend.exception.InvalidLimitUpdateException;
import com.zeta.backend.exception.UserNotFoundException;
import com.zeta.backend.model.Card;
import com.zeta.backend.model.UserProfile;
import com.zeta.backend.repository.CardRepository;
import com.zeta.backend.repository.TransactionRepository;
import com.zeta.backend.repository.UserProfileRepository;
import com.zeta.backend.util.CardApprovalUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CardLimitService implements ICardLimitService {

    private final UserProfileRepository userProfileRepository;
    private final CardRepository cardRepository;
    private final TransactionRepository transactionRepository;


    @Override
    public String updateCreditLimit(Long cardId, Double newLimit) {
        log.info("Request to update credit limit for card ID: {}, New Limit: {}", cardId, newLimit);

        Card card = getCardById(cardId);
        UserProfile profile = getUserProfileById(card.getUser().getUserId());

        double currentLimit = card.getCreditLimit();
        log.debug("Current Limit: {}, Requested Limit: {}", currentLimit, newLimit);


        if(newLimit.equals(currentLimit)) {
            log.info("No change in credit limit for card ID: {}", cardId);
            return "No change in credit limit";
        }

        if(newLimit < currentLimit) {
            log.info("Request is to decrease the credit limit for card ID: {}", cardId);
            validateLimitDecrease(card,profile,newLimit);
            return updateLimit(card, newLimit, "Credit limit decreased");
        }

        log.info("Request is to increase the credit limit for card ID: {}", cardId);
        validateLimitIncrease(card,profile,newLimit);
        return updateLimit(card, newLimit, "Credit limit increased");
    }

    private Card getCardById(Long cardId) {
        log.debug("Fetching card with ID: {}", cardId);
        return cardRepository.findById(cardId)
                .orElseThrow(() -> new CardNotFoundException("Card with ID " + cardId + " not found"));
    }

    private UserProfile getUserProfileById(Long userId) {
        log.debug("Fetching user profile with ID: {}", userId);
        return userProfileRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User Profile with ID " + userId + " not found"));
    }

    private void validateLimitIncrease(Card card, UserProfile profile,Double newLimit) {
        log.debug("Validating limit increase eligibility for card ID: {}", card.getCardId());
        String status = CardApprovalUtil.determineApplicationStatus(profile,card.getCardType(),newLimit);
        log.debug("Card approval status: {}", status);

        if(!status.equals("APPROVED")) {
            log.warn("Limit increase not approved for card ID: {}: Status={}", card.getCardId(), status);
            throw new InvalidLimitUpdateException("Requested credit limit is not eligible " + status);
        }

        validateUsageFrequency(card.getCardId(),0);
    }

    private void validateLimitDecrease(Card card, UserProfile profile,Double newLimit) {
        double minAllowedLimit = CardApprovalUtil.getMinLimitForCardAndIncome(card.getCardType(),profile.getAnnualIncome());
        log.debug("Minimum allowed limit: {}, Requested new limit: {}", minAllowedLimit, newLimit);

        if(newLimit < minAllowedLimit) {
            log.warn("Requested limit {} is below minimum allowed {} for card ID: {}", newLimit, minAllowedLimit, card.getCardId());
            throw new InvalidLimitUpdateException("Requested credit limit is less than minimum allowed limit " + minAllowedLimit);
        }
    }

    private void validateUsageFrequency(Long cardId, int threshold) {
        Long transactionCount = transactionRepository.countByCardId(cardId);
        log.debug("Transaction count for card ID {}: {}", cardId, transactionCount);

        if(transactionCount < threshold) {
            log.warn("Usage frequency too low for card ID {}: only {} transactions", cardId, transactionCount);
            throw new InvalidLimitUpdateException("Card usage is too low: only " + transactionCount +
                    " transactions found (minimum required: " + threshold + ")");
        }
    }

    private String updateLimit(Card card, Double newLimit, String message) {
        Double newAvailablelimit = card.getAvailableLimit() + (newLimit - card.getCreditLimit());
        card.setCreditLimit(newLimit);
        card.setAvailableLimit(newAvailablelimit);
        cardRepository.save(card);
        log.info("Updated credit limit for card ID {}: new limit={}, available limit={}", card.getCardId(), newLimit, newAvailablelimit);
        return message;
    }
}
