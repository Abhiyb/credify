package com.zeta.backend.service;

import com.zeta.backend.dto.TransactionCreateDTO;
import com.zeta.backend.dto.TransactionResponseDTO;
import com.zeta.backend.dto.TransactionUpdateDTO;
import com.zeta.backend.enums.InstallmentPlan;
import com.zeta.backend.exception.BadRequestException;
import com.zeta.backend.exception.CardInactiveException;
import com.zeta.backend.exception.InsufficientCreditLimitException;
import com.zeta.backend.exception.InvalidInstallmentPlanException;
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
import java.util.stream.Collectors;

/**
 * Service implementation for managing transactions, including regular and BNPL transactions.
 * Implements ITransactionService, using DTOs for secure data transfer and repository queries
 * for data access. Restricts BNPL plans to THREE, SIX, or NINE months. Includes logging
 * for debugging and monitoring.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionService implements ITransactionService {

    private final TransactionRepository transactionRepository;
    private final BNPLInstallmentRepository bnplInstallmentRepository;
    private final CardRepository cardRepository;

    /**
     * Retrieves a card by ID, throwing an exception if not found.
     * @param cardId ID of the card to retrieve.
     * @return Card entity.
     * @throws ResourceNotFoundException if card is not found.
     */
    private Card getCard(Long cardId) {
        log.debug("Fetching card with ID: {}", cardId);
        return cardRepository.findById(cardId)
                .orElseThrow(() -> {
                    log.error("Card not found for ID: {}", cardId);
                    return new ResourceNotFoundException("Card not found");
                });
    }

    /**
     * Validates the transaction amount to ensure it’s positive.
     * @param amount Transaction amount to validate.
     * @throws BadRequestException if amount is null or non-positive.
     */
    private void validateTransactionAmount(Double amount) {
        if (amount == null || amount <= 0) {
            log.error("Invalid transaction amount: {}", amount);
            throw new BadRequestException("Transaction amount must be positive");
        }
    }

    /**
     * Validates the card’s status and available limit for the transaction.
     * @param card Card entity to validate.
     * @param amount Transaction amount to check against available limit.
     * @throws CardInactiveException if card is not active.
     * @throws InsufficientCreditLimitException if card limit is insufficient.
     */
    private void validateCard(Card card, Double amount) {
        if (!"ACTIVE".equalsIgnoreCase(card.getStatus())) {
            log.error("Card is not active: {}", card.getCardId());
            throw new CardInactiveException(card.getCardId());
        }
        if (amount > card.getAvailableLimit()) {
            log.error("Insufficient available limit for card {}: requested {}, available {}",
                    card.getCardId(), amount, card.getAvailableLimit());
            throw new InsufficientCreditLimitException(amount, card.getAvailableLimit());
        }
    }

    /**
     * Validates the installment plan to ensure it’s not TWELVE months.
     * @param plan Installment plan to validate.
     * @throws InvalidInstallmentPlanException if plan is TWELVE.
     */
    private void validateInstallmentPlan(InstallmentPlan plan) {
        if (plan == InstallmentPlan.TWELVE) {
            log.error("12-month installment plan is not allowed: {}", plan);
            throw new InvalidInstallmentPlanException(plan);
        }
    }

    /**
     * Simulates a regular (non-BNPL) transaction, updating the card’s available limit.
     * @param transaction dto containing transaction details.
     * @return TransactionResponseDTO with saved transaction details.
     * @throws ResourceNotFoundException if card is not found.
     * @throws BadRequestException if amount is invalid.
     * @throws CardInactiveException if card is not active.
     * @throws InsufficientCreditLimitException if card limit is insufficient.
     */
    @Override
    @Transactional
    public TransactionResponseDTO simulateRegularTransaction(TransactionCreateDTO transaction) {
        log.info("Simulating regular transaction for card ID: {}", transaction.getCardId());

        validateTransactionAmount(transaction.getAmount());
        Card card = getCard(transaction.getCardId());
        validateCard(card, transaction.getAmount());

        // Update card’s available limit
        card.setAvailableLimit(card.getAvailableLimit() - transaction.getAmount());
        cardRepository.save(card);
        log.debug("Updated available limit for card {}: {}", card.getCardId(), card.getAvailableLimit());

        // Create transaction entity
        Transaction entity = new Transaction();
        entity.setCard(card);
        entity.setCardId(transaction.getCardId());
        entity.setAmount(transaction.getAmount());
        entity.setCategory(transaction.getCategory());
        entity.setMerchantName(transaction.getMerchantName());
        entity.setTransactionDate(LocalDate.now());
        entity.setIsBNPL(false);
        entity.setStatus("Completed");

        // Save and map to dto
        Transaction saved = transactionRepository.save(entity);
        log.info("Regular transaction saved with ID: {}", saved.getId());
        return mapToResponseDTO(saved);
    }

    /**
     * Simulates a BNPL transaction, creating installments and updating the card’s limit.
     * @param transaction dto containing transaction details.
     * @param plan Installment plan (THREE, SIX, or NINE months).
     * @return TransactionResponseDTO with saved transaction details.
     * @throws ResourceNotFoundException if card is not found.
     * @throws BadRequestException if amount is invalid.
     * @throws InvalidInstallmentPlanException if plan is TWELVE.
     * @throws CardInactiveException if card is not active.
     * @throws InsufficientCreditLimitException if card limit is insufficient.
     */
    @Override
    @Transactional
    public TransactionResponseDTO simulateBNPLTransaction(TransactionCreateDTO transaction, InstallmentPlan plan) {
        log.info("Simulating BNPL transaction for card ID: {} with plan: {}", transaction.getCardId(), plan);

        validateTransactionAmount(transaction.getAmount());
        validateInstallmentPlan(plan);
        Card card = getCard(transaction.getCardId());
        validateCard(card, transaction.getAmount());

        // Update card’s available limit
        card.setAvailableLimit(card.getAvailableLimit() - transaction.getAmount());
        cardRepository.save(card);
        log.debug("Updated available limit for card {}: {}", card.getCardId(), card.getAvailableLimit());

        // Create transaction entity
        Transaction entity = new Transaction();
        entity.setCard(card);
        entity.setCardId(transaction.getCardId());
        entity.setAmount(transaction.getAmount());
        entity.setCategory(transaction.getCategory());
        entity.setMerchantName(transaction.getMerchantName());
        entity.setTransactionDate(LocalDate.now());
        entity.setIsBNPL(true);
        entity.setStatus("Pending");

        // Save transaction and create installments
        Transaction savedTransaction = transactionRepository.save(entity);
        createInstallments(savedTransaction, plan.getMonths());
        log.info("BNPL transaction saved with ID: {}", savedTransaction.getId());
        return mapToResponseDTO(savedTransaction);
    }

    /**
     * Creates installments for a BNPL transaction based on the number of months.
     * Rounds installment amounts to two decimal places and adjusts the last installment
     * to account for rounding errors.
     * @param transaction The BNPL transaction entity.
     * @param installmentCount Number of installments (3, 6, or 9).
     */
    @Transactional
    private void createInstallments(Transaction transaction, int installmentCount) {
        log.debug("Creating {} installments for transaction ID: {}", installmentCount, transaction.getId());

        double totalAmount = transaction.getAmount();
        double installmentAmount = Math.round((totalAmount / installmentCount) * 100.0) / 100.0; // Round to 2 decimals
        LocalDate firstDueDate = LocalDate.now().plusMonths(1);
        double totalCreated = 0;
        List<BNPLInstallment> installments = new ArrayList<>();

        for (int i = 1; i <= installmentCount; i++) {
            // Adjust last installment amount to account for rounding errors
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
            log.debug("Created installment {} for transaction ID: {}, amount: {}, due date: {}",
                    i, transaction.getId(), amount, installment.getDueDate());
        }

        bnplInstallmentRepository.saveAll(installments);
        log.info("Created {} installments for transaction ID: {}", installmentCount, transaction.getId());
    }

    /**
     * Retrieves transaction history for a specific card.
     * @param cardId ID of the card to fetch transactions for.
     * @return List of TransactionResponseDTOs.
     */
    @Override
    public List<TransactionResponseDTO> getTransactionHistoryByCardId(Long cardId) {
        log.info("Fetching transaction history for card ID: {}", cardId);
        List<Transaction> transactions = transactionRepository.findByCardId(cardId);
        log.debug("Found {} transactions for card ID: {}", transactions.size(), cardId);
        return transactions.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves all transactions in the system.
     * @return List of TransactionResponseDTOs.
     */
    @Override
    public List<TransactionResponseDTO> getAllTransactions() {
        log.info("Fetching all transactions");
        List<Transaction> transactions = transactionRepository.findAll();
        log.debug("Found {} transactions", transactions.size());
        return transactions.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a transaction by ID.
     * @param id ID of the transaction to fetch.
     * @return TransactionResponseDTO.
     * @throws ResourceNotFoundException if transaction is not found.
     */
    @Override
    public TransactionResponseDTO getTransactionById(Long id) {
        log.info("Fetching transaction with ID: {}", id);
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Transaction not found for ID: {}", id);
                    return new ResourceNotFoundException("Transaction not found");
                });
        return mapToResponseDTO(transaction);
    }

    /**
     * Updates an existing transaction with provided details.
     * @param id ID of the transaction to update.
     * @param updatedTransaction dto containing updated transaction details.
     * @return TransactionResponseDTO with updated details.
     * @throws ResourceNotFoundException if transaction is not found.
     * @throws BadRequestException if update data is invalid.
     */
    @Override
    @Transactional
    public TransactionResponseDTO updateTransaction(Long id, TransactionUpdateDTO updatedTransaction) {
        log.info("Updating transaction with ID: {}", id);
        Transaction existing = transactionRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Transaction not found for ID: {}", id);
                    return new ResourceNotFoundException("Transaction not found");
                });

        // Update fields
        existing.setAmount(updatedTransaction.getAmount());
        existing.setCategory(updatedTransaction.getCategory());
        existing.setMerchantName(updatedTransaction.getMerchantName());
        existing.setTransactionDate(updatedTransaction.getTransactionDate() != null
                ? updatedTransaction.getTransactionDate() : LocalDate.now());
        existing.setStatus(updatedTransaction.getStatus() != null
                ? updatedTransaction.getStatus() : existing.getStatus());
        existing.setIsBNPL(updatedTransaction.isBNPL());

        // Save and map to dto
        Transaction saved = transactionRepository.save(existing);
        log.info("Transaction updated with ID: {}", saved.getId());
        return mapToResponseDTO(saved);
    }

    /**
     * Deletes a transaction by ID.
     * @param id ID of the transaction to delete.
     * @throws ResourceNotFoundException if transaction is not found.
     */
    @Override
    @Transactional
    public void deleteTransaction(Long id) {
        log.info("Deleting transaction with ID: {}", id);
        Transaction existing = transactionRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Transaction not found for ID: {}", id);
                    return new ResourceNotFoundException("Transaction not found");
                });
        transactionRepository.delete(existing);
        log.info("Transaction deleted with ID: {}", id);
    }

    /**
     * Maps a Transaction entity to a TransactionResponseDTO for frontend response.
     * @param transaction Transaction entity to map.
     * @return TransactionResponseDTO with relevant fields.
     */
    private TransactionResponseDTO mapToResponseDTO(Transaction transaction) {
        log.debug("Mapping transaction ID: {} to response dto", transaction.getId());
        TransactionResponseDTO dto = new TransactionResponseDTO();
        dto.setId(transaction.getId());
        dto.setCardId(transaction.getCardId());
        dto.setAmount(transaction.getAmount());
        dto.setCategory(transaction.getCategory());
        dto.setMerchantName(transaction.getMerchantName());
        dto.setTransactionDate(transaction.getTransactionDate());
        dto.setStatus(transaction.getStatus());
        dto.setBNPL(transaction.getIsBNPL());

        return dto;
    }
}