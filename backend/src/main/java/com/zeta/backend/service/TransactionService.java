package com.zeta.backend.service;

import com.zeta.backend.enums.InstallmentPlan;
import com.zeta.backend.exception.ResourceNotFoundException;
import com.zeta.backend.model.BNPLInstallment;
import com.zeta.backend.model.Card;
import com.zeta.backend.model.Transaction;
import com.zeta.backend.repository.BNPLInstallmentRepository;
import com.zeta.backend.repository.CardRepository;
import com.zeta.backend.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionService implements ITransactionService {

    private final TransactionRepository transactionRepository;
    private final BNPLInstallmentRepository bnplInstallmentRepository;
    private final CardRepository cardRepository;

    // Fetch a card by ID, throw error if not found
    private Card getCard(Long cardId) {
        return cardRepository.findById(cardId)
                .orElseThrow(() -> new ResourceNotFoundException("Card not found"));
    }

    // Validate that the transaction amount is positive
    private void validateTransactionAmount(Double amount) {
        if (amount == null || amount <= 0) {
            throw new IllegalArgumentException("Transaction amount must be positive");
        }
    }

    // Check if card is active and has enough limit
    private void validateCard(Card card, Double amount) {
        if (!"ACTIVE".equalsIgnoreCase(card.getStatus())) {
            throw new IllegalStateException("Card is not active");
        }
        if (amount > card.getAvailableLimit()) {
            throw new IllegalStateException("Insufficient available credit limit");
        }
    }

    // Simulates a standard (non-BNPL) transaction
    @Override
    @Transactional
    public Transaction simulateRegularTransaction(Transaction transaction) {
        log.info("Simulating regular transaction for card {}", transaction.getCard().getCardId());

        validateTransactionAmount(transaction.getAmount());
        Card card = getCard(transaction.getCard().getCardId());
        validateCard(card, transaction.getAmount());

        card.setAvailableLimit(card.getAvailableLimit() - transaction.getAmount());
        cardRepository.save(card);

        transaction.setIsBNPL(false);
        if (transaction.getStatus() == null) {
            transaction.setStatus("Completed");
        }

        Transaction saved = transactionRepository.save(transaction);
        log.info("Regular transaction saved with ID {}", saved.getId());
        return saved;
    }

    // Simulates a BNPL transaction and creates installments
    @Override
    @Transactional
    public Transaction simulateBNPLTransaction(Transaction transaction, InstallmentPlan plan) {
        log.info("Simulating BNPL transaction for card {} with plan {}", transaction.getCard().getCardId(), plan);

        validateTransactionAmount(transaction.getAmount());
        Card card = getCard(transaction.getCard().getCardId());
        validateCard(card, transaction.getAmount());

        card.setAvailableLimit(card.getAvailableLimit() - transaction.getAmount());
        cardRepository.save(card);

        transaction.setIsBNPL(true);
        if (transaction.getStatus() == null) {
            transaction.setStatus("Pending");
        }

        Transaction savedTransaction = transactionRepository.save(transaction);
        createInstallments(savedTransaction, plan.getMonths());
        log.info("BNPL transaction saved with ID {}", savedTransaction.getId());

        return savedTransaction;
    }

    // Create installment entries for a BNPL transaction
    @Transactional
    private void createInstallments(Transaction transaction, int installmentCount) {
        double totalAmount = transaction.getAmount();
        double installmentAmount = Math.round((totalAmount / installmentCount) * 100.0) / 100.0;
        LocalDate firstDueDate = LocalDate.now().plusMonths(1);

        double totalCreated = 0;
        List<BNPLInstallment> installments = new ArrayList<>();

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

            installments.add(installment);
        }

        bnplInstallmentRepository.saveAll(installments);
        log.info("{} installments created for transaction ID {}", installmentCount, transaction.getId());
    }

    // Retrieve all transactions made using a specific card
    @Override
    public List<Transaction> getTransactionHistoryByCardId(Long cardId) {
        log.info("Fetching transaction history for card {}", cardId);
        return transactionRepository.findByCardId(cardId);
    }

    // Fetch all transactions from the database
    @Override
    public List<Transaction> getAllTransactions() {
        log.info("Fetching all transactions");
        return transactionRepository.findAll();
    }

    // Retrieve transaction by ID
    @Override
    public Transaction getTransactionById(Long id) {
        log.info("Fetching transaction by ID {}", id);
        return transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));
    }

    // Update an existing transaction
    @Override
    public Transaction updateTransaction(Long id, Transaction updatedTransaction) {
        log.info("Updating transaction ID {}", id);

        Transaction existing = getTransactionById(id);
        existing.setAmount(updatedTransaction.getAmount());
        existing.setCategory(updatedTransaction.getCategory());
        existing.setMerchantName(updatedTransaction.getMerchantName());
        existing.setTransactionDate(updatedTransaction.getTransactionDate());
        existing.setStatus(updatedTransaction.getStatus());
        existing.setIsBNPL(updatedTransaction.getIsBNPL());

        return transactionRepository.save(existing);
    }

    // Delete a transaction by ID
    @Override
    public void deleteTransaction(Long id) {
        log.info("Deleting transaction ID {}", id);

        Transaction existing = getTransactionById(id);
        transactionRepository.delete(existing);
    }
}
