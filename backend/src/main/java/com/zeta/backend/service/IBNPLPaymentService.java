package com.zeta.backend.service;

import com.zeta.backend.dto.BNPLInstallmentCreateDTO;
import com.zeta.backend.dto.BNPLInstallmentResponseDTO;
import com.zeta.backend.dto.BNPLInstallmentUpdateDTO;

import java.util.List;

/**
 * Interface for BNPL installment service operations, managing installment payments and queries.
 * Provides methods for paying, retrieving, creating, updating, and deleting installments using DTOs
 * for secure data transfer. Aligns with BNPLInstallmentRepository for data access.
 */
public interface IBNPLPaymentService {

    /**
     * Processes a payment for an installment, marking it as paid if the amount is valid.
     * @param installmentId ID of the installment to pay.
     * @param amount Payment amount provided by the client.
     * @return BNPLInstallmentResponseDTO with updated installment details.
     * @throws com.zeta.backend.exception.ResourceNotFoundException if installment is not found.
     * @throws com.zeta.backend.exception.BadRequestException if payment amount is invalid or installment is already paid.
     */
    BNPLInstallmentResponseDTO payInstallment(Long installmentId, Double amount);

    /**
     * Retrieves pending (unpaid) installments for a specific transaction, ordered by installment number.
     * @param transactionId ID of the transaction to fetch pending installments for.
     * @return List of BNPLInstallmentResponseDTOs representing unpaid installments.
     */
    List<BNPLInstallmentResponseDTO> getPendingInstallmentsByTransactionId(Long transactionId);

    /**
     * Retrieves overdue installments for a specific card, where due date is before today and unpaid.
     * @param cardId ID of the card to fetch overdue installments for.
     * @return List of BNPLInstallmentResponseDTOs representing overdue installments.
     */
    List<BNPLInstallmentResponseDTO> getOverdueInstallmentsByCardId(Long cardId);

    /**
     * Retrieves all installments in the system.
     * @return List of BNPLInstallmentResponseDTOs for all installments.
     */
    List<BNPLInstallmentResponseDTO> getAllInstallments();

    /**
     * Retrieves an installment by its ID.
     * @param id ID of the installment to fetch.
     * @return BNPLInstallmentResponseDTO with installment details.
     * @throws com.zeta.backend.exception.ResourceNotFoundException if installment is not found.
     */
    BNPLInstallmentResponseDTO getInstallmentById(Long id);

    /**
     * Creates a new BNPL installment, typically for manual creation or testing.
     * @param installment dto containing installment details (transaction ID, amount, due date, etc.).
     * @return BNPLInstallmentResponseDTO with saved installment details.
     * @throws com.zeta.backend.exception.ResourceNotFoundException if associated transaction is not found.
     * @throws com.zeta.backend.exception.BadRequestException if installment data is invalid.
     */
    BNPLInstallmentResponseDTO createInstallment(BNPLInstallmentCreateDTO installment);

    /**
     * Updates an existing BNPL installment with provided details.
     * @param id ID of the installment to update.
     * @param updated dto containing updated installment details.
     * @return BNPLInstallmentResponseDTO with updated installment details.
     * @throws com.zeta.backend.exception.ResourceNotFoundException if installment or transaction is not found.
     * @throws com.zeta.backend.exception.BadRequestException if update data is invalid.
     */
    BNPLInstallmentResponseDTO updateInstallment(Long id, BNPLInstallmentUpdateDTO updated);

    /**
     * Deletes an existing BNPL installment.
     * @param id ID of the installment to delete.
     * @throws com.zeta.backend.exception.ResourceNotFoundException if installment is not found.
     */
    void deleteInstallment(Long id);

    /**
     * Retrieves all installments for a specific transaction.
     * @param transactionId ID of the transaction to fetch installments for.
     * @return List of BNPLInstallmentResponseDTOs representing all installments for the transaction.
     */
    List<BNPLInstallmentResponseDTO> getAllInstallmentsByTransactionId(Long transactionId);
}