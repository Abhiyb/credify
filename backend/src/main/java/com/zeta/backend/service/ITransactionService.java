package com.zeta.backend.service;

import com.zeta.backend.dto.TransactionCreateDTO;
import com.zeta.backend.dto.TransactionResponseDTO;
import com.zeta.backend.dto.TransactionUpdateDTO;
import com.zeta.backend.enums.InstallmentPlan;

import java.util.List;

/**
 * Interface for transaction service operations, managing regular and Buy Now, Pay Later (BNPL) transactions.
 * Provides methods for creating, retrieving, updating, and deleting transactions using DTOs for secure data transfer.
 */
public interface ITransactionService {

    /**
     * Simulates a regular (non-BNPL) transaction, validating card status and available limit.
     * @param transaction dto containing transaction details (card ID, amount, category, merchant, etc.).
     * @return TransactionResponseDTO with saved transaction details.
     * @throws com.zeta.backend.exception.ResourceNotFoundException if card is not found.
     * @throws com.zeta.backend.exception.BadRequestException if amount is invalid.
     * @throws com.zeta.backend.exception.CardInactiveException if card is not active.
     * @throws com.zeta.backend.exception.InsufficientCreditLimitException if card limit is insufficient.
     */
    TransactionResponseDTO simulateRegularTransaction(TransactionCreateDTO transaction);

    /**
     * Simulates a BNPL transaction, creating installments and validating card status and limit.
     * Only THREE, SIX, or NINE month plans are supported; TWELVE is not allowed.
     * @param transaction dto containing transaction details (card ID, amount, category, merchant, etc.).
     * @param plan Installment plan (THREE, SIX, or NINE months).
     * @return TransactionResponseDTO with saved transaction details.
     * @throws com.zeta.backend.exception.ResourceNotFoundException if card is not found.
     * @throws com.zeta.backend.exception.BadRequestException if amount is invalid.
     * @throws com.zeta.backend.exception.InvalidInstallmentPlanException if plan is TWELVE.
     * @throws com.zeta.backend.exception.CardInactiveException if card is not active.
     * @throws com.zeta.backend.exception.InsufficientCreditLimitException if card limit is insufficient.
     */
    TransactionResponseDTO simulateBNPLTransaction(TransactionCreateDTO transaction, InstallmentPlan plan);

    /**
     * Retrieves transaction history for a specific card.
     * @param cardId ID of the card to fetch transactions for.
     * @return List of TransactionResponseDTOs representing the card's transaction history.
     */
    List<TransactionResponseDTO> getTransactionHistoryByCardId(Long cardId);

    /**
     * Retrieves all transactions in the system.
     * @return List of TransactionResponseDTOs for all transactions.
     */
    List<TransactionResponseDTO> getAllTransactions();

    /**
     * Retrieves a transaction by its ID.
     * @param id ID of the transaction to fetch.
     * @return TransactionResponseDTO with transaction details.
     * @throws com.zeta.backend.exception.ResourceNotFoundException if transaction is not found.
     */
    TransactionResponseDTO getTransactionById(Long id);

    /**
     * Updates an existing transaction with provided details.
     * @param id ID of the transaction to update.
     * @param updatedTransaction dto containing updated transaction details.
     * @return TransactionResponseDTO with updated transaction details.
     * @throws com.zeta.backend.exception.ResourceNotFoundException if transaction is not found.
     * @throws com.zeta.backend.exception.BadRequestException if update data is invalid.
     */
    TransactionResponseDTO updateTransaction(Long id, TransactionUpdateDTO updatedTransaction);

    /**
     * Deletes a transaction by its ID.
     * @param id ID of the transaction to delete.
     * @throws com.zeta.backend.exception.ResourceNotFoundException if transaction is not found.
     */
    void deleteTransaction(Long id);
}