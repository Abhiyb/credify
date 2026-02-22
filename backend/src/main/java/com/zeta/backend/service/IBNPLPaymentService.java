package com.zeta.backend.service;

import com.zeta.backend.dto.BNPLInstallmentCreateDTO;
import com.zeta.backend.dto.BNPLInstallmentResponseDTO;
import com.zeta.backend.dto.BNPLInstallmentUpdateDTO;

import java.util.List;

/**
 * Interface for BNPL installment service operations, managing installment payments and queries.
 * Methods that require user ownership include `userEmail` for JWT-based authorization.
 * Admin-level methods do not require `userEmail`.
 */
public interface IBNPLPaymentService {

    /**
     * Pay an installment.
     * Only the owner of the card/transaction can pay.
     * @param installmentId ID of the installment.
     * @param amount Amount to pay.
     * @param userEmail Email of the authenticated user (from JWT).
     * @return Updated BNPLInstallmentResponseDTO.
     */
    BNPLInstallmentResponseDTO payInstallment(Long installmentId, Double amount, String userEmail);

    /**
     * Fetch pending (unpaid) installments for a transaction.
     * User can only fetch installments of their own transactions.
     * @param transactionId Transaction ID.
     * @param userEmail Email of the authenticated user (from JWT).
     * @return List of pending installments.
     */
    List<BNPLInstallmentResponseDTO> getPendingInstallmentsByTransactionId(Long transactionId, String userEmail);

    /**
     * Fetch overdue installments for a card.
     * User can only fetch installments of their own cards.
     * @param cardId Card ID.
     * @param userEmail Email of the authenticated user (from JWT).
     * @return List of overdue installments.
     */
    List<BNPLInstallmentResponseDTO> getOverdueInstallmentsByCardId(Long cardId, String userEmail);

    /**
     * Admin-level method: Get all installments.
     * No userEmail required.
     * @return List of all installments.
     */
    List<BNPLInstallmentResponseDTO> getAllInstallments();

    /**
     * Get a specific installment by its ID.
     * Admin-level or used internally.
     * @param id Installment ID.
     * @return BNPLInstallmentResponseDTO.
     */
    BNPLInstallmentResponseDTO getInstallmentById(Long id);

    /**
     * Create a new installment.
     * Admin-level or system-generated; no userEmail needed.
     * @param installment DTO with installment details.
     * @return Created BNPLInstallmentResponseDTO.
     */
    BNPLInstallmentResponseDTO createInstallment(BNPLInstallmentCreateDTO installment);

    /**
     * Update an existing installment.
     * Admin-level or system-generated; no userEmail needed.
     * @param id Installment ID.
     * @param updated DTO with updated data.
     * @return Updated BNPLInstallmentResponseDTO.
     */
    BNPLInstallmentResponseDTO updateInstallment(Long id, BNPLInstallmentUpdateDTO updated);

    /**
     * Delete an installment.
     * Admin-level or system-generated.
     * @param id Installment ID.
     */
    void deleteInstallment(Long id);

    /**
     * Fetch all installments for a specific transaction.
     * Admin-level or internal use; no userEmail required.
     * @param transactionId Transaction ID.
     * @return List of installments.
     */
    List<BNPLInstallmentResponseDTO> getAllInstallmentsByTransactionId(Long transactionId);
}
