package com.zeta.backend.service;

import com.zeta.backend.exception.ResourceNotFoundException;
import com.zeta.backend.model.BNPLInstallment;
import com.zeta.backend.model.Card;
import com.zeta.backend.model.Transaction;
import com.zeta.backend.repository.BNPLInstallmentRepository;
import com.zeta.backend.repository.CardRepository;
import com.zeta.backend.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService implements ITransactionService{

    private final TransactionRepository transactionRepository;
    private final BNPLInstallmentRepository bnplInstallmentRepository;
    private final CardRepository cardRepository;

    private Card getCard(Long cardId) {
        return cardRepository.findById(cardId)
                .orElseThrow(() -> new ResourceNotFoundException("Card not found"));
    }

    @Transactional
    public Transaction simulateTransaction(Transaction transaction, int installmentCount) {
        if (transaction.getAmount() == null || transaction.getAmount() <= 0) {
            throw new IllegalArgumentException("Transaction amount must be positive");
        }

        Card card = getCard(transaction.getCard().getCardId());

        if (!"ACTIVE".equalsIgnoreCase(card.getStatus())) {
            throw new IllegalStateException("Card is not active");
        }

        if (transaction.getAmount() > card.getAvailableLimit()) {
            throw new IllegalStateException("Insufficient available credit limit");
        }

        // Deduct amount from available limit immediately
        card.setAvailableLimit(card.getAvailableLimit() - transaction.getAmount());
        cardRepository.save(card);

        // Save the transaction
        Transaction savedTransaction = transactionRepository.save(transaction);

        // If BNPL is chosen, create installments
        if (Boolean.TRUE.equals(transaction.getIsBNPL())) {
            if (installmentCount < 1) {
                throw new IllegalArgumentException("Installment count must be at least 1");
            }
            createInstallments(savedTransaction, installmentCount);
        }

        return savedTransaction;
    }

    private void createInstallments(Transaction transaction, int installmentCount) {
        double totalAmount = transaction.getAmount();
        double installmentAmount = Math.round((totalAmount / installmentCount) * 100.0) / 100.0; // Round to 2 decimals
        LocalDate firstDueDate = LocalDate.now().plusMonths(1);

        double totalCreated = 0;
        for (int i = 1; i <= installmentCount; i++) {
            double amount = (i == installmentCount) ? totalAmount - totalCreated : installmentAmount;
            totalCreated += amount;

            BNPLInstallment installment = BNPLInstallment.builder()
                    .transaction(transaction)
                    .installmentNumber(i)
                    .amount(amount)
                    .dueDate(firstDueDate.plusMonths(i - 1))
                    .isPaid(false)
                    .build();

            bnplInstallmentRepository.save(installment);
        }
    }

    public List<Transaction> getTransactionHistoryByCardId(Long cardId) {
        return transactionRepository.findByCardId(cardId);
    }
}
