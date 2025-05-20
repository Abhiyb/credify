package com.zeta.backend.service;

import com.zeta.backend.enums.InstallmentPlan;
import com.zeta.backend.exception.ResourceNotFoundException;
import com.zeta.backend.model.BNPLInstallment;
import com.zeta.backend.model.Transaction;
import com.zeta.backend.repository.BNPLInstallmentRepository;
import com.zeta.backend.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService implements ITransactionService {

    private final TransactionRepository transactionRepository;
    private final BNPLInstallmentRepository bnplInstallmentRepository;
    private final CardRepository cardRepository;

    private Card getCard(Long cardId) {
        return cardRepository.findById(cardId)
                .orElseThrow(() -> new ResourceNotFoundException("Card not found"));
    }

    private void validateTransactionAmount(Double amount) {
        if (amount == null || amount <= 0) {
            throw new IllegalArgumentException("Transaction amount must be positive");
        }
    }

    private void validateCard(Card card, Double amount) {
        if (!"ACTIVE".equalsIgnoreCase(card.getStatus())) {
            throw new IllegalStateException("Card is not active");
        }
        if (amount > card.getAvailableLimit()) {
            throw new IllegalStateException("Insufficient available credit limit");
        }
    }

    @Override
    @Transactional
    public Transaction simulateRegularTransaction(Transaction transaction) {
        validateTransactionAmount(transaction.getAmount());
        Card card = getCard(transaction.getCard().getCardId());
        validateCard(card, transaction.getAmount());

        card.setAvailableLimit(card.getAvailableLimit() - transaction.getAmount());
        cardRepository.save(card);

        transaction.setIsBNPL(false);
        return transactionRepository.save(transaction);
    }

    @Override
    @Transactional
    public Transaction simulateBNPLTransaction(Transaction transaction, InstallmentPlan plan) {
        validateTransactionAmount(transaction.getAmount());

        Card card = getCard(transaction.getCard().getCardId());
        validateCard(card, transaction.getAmount());

        card.setAvailableLimit(card.getAvailableLimit() - transaction.getAmount());
        cardRepository.save(card);

        transaction.setIsBNPL(true);
        Transaction savedTransaction = transactionRepository.save(transaction);
        createInstallments(savedTransaction, plan.getMonths());
        return savedTransaction;
    }

    private void createInstallments(Transaction transaction, int installmentCount) {
        double totalAmount = transaction.getAmount();
        double installmentAmount = Math.round((totalAmount / installmentCount) * 100.0) / 100.0;
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

    @Override
    public List<Transaction> getTransactionHistoryByCardId(Long cardId) {
        return transactionRepository.findByCardId(cardId);
    }

    @Override
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    @Override
    public Transaction getTransactionById(Long id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));
    }

    @Override
    public Transaction updateTransaction(Long id, Transaction updatedTransaction) {
        Transaction existing = getTransactionById(id);
        existing.setAmount(updatedTransaction.getAmount());
        existing.setCategory(updatedTransaction.getCategory());
        existing.setMerchantName(updatedTransaction.getMerchantName());
        existing.setTransactionDate(updatedTransaction.getTransactionDate());
        return transactionRepository.save(existing);
    }

    @Override
    public void deleteTransaction(Long id) {
        Transaction existing = getTransactionById(id);
        transactionRepository.delete(existing);
    }
}
