package com.zeta.backend.service.implementation;

import com.zeta.backend.dto.CardDTO;
import com.zeta.backend.exception.CardNotFoundException;
import com.zeta.backend.exception.InvalidCardLimitException;
import com.zeta.backend.model.Card;
import com.zeta.backend.model.UserProfile;
import com.zeta.backend.repository.CardRepository;
import com.zeta.backend.repository.TransactionRepository;
import com.zeta.backend.service.ICardService;
import com.zeta.backend.util.CardApprovalUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CardServiceImpl implements ICardService {

    private final CardRepository cardRepository;
    private final TransactionRepository transactionRepository; // Needed for usage frequency check

    @Override
    public List<CardDTO> getCardDetailsByUserId(Long userId) {
        List<Card> cards = cardRepository.findByApplicationUserUserId(userId);
        return cards.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    public CardDTO putCardByUserId(Long cardId, String status) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new CardNotFoundException("Card not found with ID: " + cardId));

        card.setStatus(status.toUpperCase());
        return mapToDto(cardRepository.save(card));
    }

    @Override
    public CardDTO updateCardLimit(Long cardId, Double newLimit) {
        if (newLimit <= 0) {
            throw new InvalidCardLimitException("New limit must be greater than 0");
        }

        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new CardNotFoundException("Card not found with ID: " + cardId));

        UserProfile profile = card.getUser(); // Already linked to card
        double currentLimit = card.getCreditLimit();

        if (newLimit.equals(currentLimit)) {
            return mapToDto(card); // No change
        }

        if (newLimit > currentLimit) {
            // Limit increase validation
            String status = CardApprovalUtil.determineApplicationStatus(profile, card.getCardType(), newLimit);
            if (!"APPROVED".equals(status)) {
                throw new InvalidCardLimitException("Requested credit limit is not eligible: " + status);
            }

            // Check usage frequency
            Long transactionCount = transactionRepository.countByCardId(cardId);
            if (transactionCount < 1) {
                throw new InvalidCardLimitException("Card usage is too low: only " + transactionCount + " transactions found");
            }
        } else {
            // Limit decrease validation
            double minAllowedLimit = CardApprovalUtil.getMinLimitForCardAndIncome(card.getCardType(), profile.getAnnualIncome());
            if (newLimit < minAllowedLimit) {
                throw new InvalidCardLimitException("Requested limit is below minimum allowed: " + minAllowedLimit);
            }
        }

        // Update card
        double newAvailable = card.getAvailableLimit() + (newLimit - currentLimit);
        card.setCreditLimit(newLimit);
        card.setAvailableLimit(newAvailable);
        cardRepository.save(card);

        return mapToDto(card);
    }

    @Override
    public void validateCardOwnership(Long cardId, String userEmail) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new CardNotFoundException("Card not found with ID: " + cardId));

        if (!card.getUser().getEmail().equals(userEmail)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Unauthorized: This card does not belong to you");
        }
    }

    private CardDTO mapToDto(Card card) {
        return CardDTO.builder()
                .cardId(card.getCardId())
                .cardNumber(card.getCardNumber())
                .cardType(card.getCardType())
                .status(card.getStatus())
                .creditLimit(card.getCreditLimit())
                .availableLimit(card.getAvailableLimit())
                .expiryDate(card.getExpiryDate())
                .cardHolderName(card.getUser() != null ? card.getUser().getFullName() : "Unknown")
                .cvv(card.getCvv())
                .build();
    }


}
