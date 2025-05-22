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
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class CardLimitService implements ICardLimitService {

    private final UserProfileRepository userProfileRepository;
    private final CardRepository cardRepository;
    private final TransactionRepository transactionRepository;


    @Override
    public String updateCreditLimit(Long cardId, Double newLimit) {

        Card card = getCardById(cardId);
        UserProfile profile = getUserProfileById(card.getUser().getUserId());

        double currentLimit = card.getCreditLimit();

        if(newLimit.equals(currentLimit)) {
            return "No change in credit limit";
        }

        if(newLimit < currentLimit) {
            validateLimitDecrease(card,profile,newLimit);
            return updateLimit(card, newLimit, "Credit limit decreased");
        }

        validateLimitIncrease(card,profile,newLimit);
        return updateLimit(card, newLimit, "Credit limit increased");
    }

    private Card getCardById(Long cardId) {
        return cardRepository.findById(cardId)
                .orElseThrow(() -> new CardNotFoundException("Card with ID " + cardId + " not found"));
    }

    private UserProfile getUserProfileById(Long userId) {
        return userProfileRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User Profile with ID " + userId + " not found"));
    }

    private void validateLimitIncrease(Card card, UserProfile profile,Double newLimit) {
        String status = CardApprovalUtil.determineApplicationStatus(profile,card.getCardType(),newLimit);
        if(!status.equals("APPROVED")) {
            throw new InvalidLimitUpdateException("Requested credit limit is not eligible " + status);
        }

        validateUsageFrequency(card.getCardId(),0);
    }

    private void validateLimitDecrease(Card card, UserProfile profile,Double newLimit) {
        double minAllowedLimit = CardApprovalUtil.getMinLimitForCardAndIncome(card.getCardType(),profile.getAnnualIncome());
        if(newLimit < minAllowedLimit) {
            throw new InvalidLimitUpdateException("Requested credit limit is less than minimum allowed limit " + minAllowedLimit);
        }
    }

    private void validateUsageFrequency(Long cardId, int threshold) {
        Long transactionCount = transactionRepository.countByCardId(cardId);
        if(transactionCount < threshold) {
            throw new InvalidLimitUpdateException("Card usage is too low: only " + transactionCount +
                    " transactions found (minimum required: " + threshold + ")");
        }
    }

    private String updateLimit(Card card, Double newLimit, String message) {
        card.setCreditLimit(newLimit);
        cardRepository.save(card);
        return message;
    }
}
