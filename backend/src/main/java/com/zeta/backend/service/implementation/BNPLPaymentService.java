package com.zeta.backend.service.implementation;

import com.zeta.backend.dto.BNPLInstallmentCreateDTO;
import com.zeta.backend.dto.BNPLInstallmentResponseDTO;
import com.zeta.backend.dto.BNPLInstallmentUpdateDTO;
import com.zeta.backend.exception.BadRequestException;
import com.zeta.backend.exception.ResourceNotFoundException;
import com.zeta.backend.model.BNPLInstallment;
import com.zeta.backend.model.Transaction;
import com.zeta.backend.repository.BNPLInstallmentRepository;
import com.zeta.backend.repository.CardRepository;
import com.zeta.backend.repository.TransactionRepository;
import com.zeta.backend.service.IBNPLPaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service implementation for managing BNPL installments, handling payments and queries.
 * Implements IBNPLPaymentService, using DTOs for secure data transfer and repository queries
 * for data access. Includes logging for debugging and monitoring.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BNPLPaymentService implements IBNPLPaymentService {

    private final BNPLInstallmentRepository installmentRepository;
    private final TransactionRepository transactionRepository;
    private final LateFeeCalculatorService lateFeeCalculatorService;
    private final CardRepository cardRepository;

    /**
     * Processes a payment for an installment, marking it as paid if the amount is valid.
     * Validates that the installment exists, is unpaid, and the payment amount matches.
     * @param installmentId ID of the installment to pay.
     * @param amount Payment amount provided by the client.
     * @return BNPLInstallmentResponseDTO with updated installment details.
     * @throws ResourceNotFoundException if installment is not found.
     * @throws BadRequestException if installment is already paid or payment amount is invalid.
     */
    @Override
    @Transactional
    public BNPLInstallmentResponseDTO payInstallment(Long installmentId, Double amount, String userEmail) {
        BNPLInstallment installment = installmentRepository.findById(installmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Installment not found"));

        if (!installment.getTransaction().getCard().getUser().getEmail().equals(userEmail)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Unauthorized to pay this installment");
        }

        if (installment.getIsPaid()) {
            throw new BadRequestException("Installment is already paid");
        }

        if (Math.abs(amount - installment.getAmount()) > 0.01) {
            throw new BadRequestException("Payment amount must match installment amount");
        }

        installment.setIsPaid(true);
        BNPLInstallment saved = installmentRepository.save(installment);
        return mapToResponseDTO(saved);
    }


    /**
     * Retrieves pending (unpaid) installments for a specific transaction, ordered by installment number.
     * @param transactionId ID of the transaction to fetch pending installments for.
     * @return List of BNPLInstallmentResponseDTOs representing unpaid installments.
     */
    @Override
    public List<BNPLInstallmentResponseDTO> getPendingInstallmentsByTransactionId(Long transactionId, String userEmail) {
        log.info("Fetching pending installments for transaction ID: {}", transactionId);
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));
        if (!transaction.getCard().getUser().getEmail().equals(userEmail)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Unauthorized to view installments");
        }
        List<BNPLInstallment> installments = installmentRepository.findByTransaction_IdAndIsPaidFalse(transactionId);
        return installments.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }


    /**
     * Retrieves overdue installments for a specific card, where due date is before today and unpaid.
     * @param cardId ID of the card to fetch overdue installments for.
     * @return List of BNPLInstallmentResponseDTOs representing overdue installments.
     */
    @Override
    public List<BNPLInstallmentResponseDTO> getOverdueInstallmentsByCardId(Long cardId, String userEmail) {
        log.info("Fetching overdue installments for card ID: {}", cardId);
        // You need a way to fetch the card owner email from cardId, assuming you have CardRepository
        // Example:
        String cardOwnerEmail = cardRepository.findById(cardId)
                .orElseThrow(() -> new ResourceNotFoundException("Card not found"))
                .getUser().getEmail();
        if (!cardOwnerEmail.equals(userEmail)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Unauthorized to view installments");
        }

        LocalDate today = LocalDate.now();
        List<BNPLInstallment> installments = installmentRepository
                .findByTransaction_Card_CardIdAndIsPaidFalseAndDueDateBefore(cardId, today);

        return installments.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }


    /**
     * Retrieves all installments in the system.
     * @return List of BNPLInstallmentResponseDTOs for all installments.
     */
    @Override
    public List<BNPLInstallmentResponseDTO> getAllInstallments() {
        log.info("Fetching all installments");
        List<BNPLInstallment> installments = installmentRepository.findAll();
        log.debug("Found {} installments", installments.size());
        return installments.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves an installment by its ID.
     * @param id ID of the installment to fetch.
     * @return BNPLInstallmentResponseDTO with installment details.
     * @throws ResourceNotFoundException if installment is not found.
     */
    @Override
    public BNPLInstallmentResponseDTO getInstallmentById(Long id) {
        log.info("Fetching installment with ID: {}", id);
        BNPLInstallment installment = installmentRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Installment not found for ID: {}", id);
                    return new ResourceNotFoundException("Installment not found");
                });
        return mapToResponseDTO(installment);
    }

    /**
     * Creates a new BNPL installment, typically for manual creation or testing.
     * Validates the associated transaction exists.
     * @param installment dto containing installment details (transaction ID, amount, due date, etc.).
     * @return BNPLInstallmentResponseDTO with saved installment details.
     * @throws ResourceNotFoundException if associated transaction is not found.
     * @throws BadRequestException if installment data is invalid.
     */
    @Override
    @Transactional
    public BNPLInstallmentResponseDTO createInstallment(BNPLInstallmentCreateDTO installment) {
        log.info("Creating new installment for transaction ID: {}", installment.getTransactionId());
        Transaction transaction = transactionRepository.findById(installment.getTransactionId())
                .orElseThrow(() -> {
                    log.error("Transaction not found for ID: {}", installment.getTransactionId());
                    return new ResourceNotFoundException("Transaction not found");
                });

        BNPLInstallment newInstallment = new BNPLInstallment();
        newInstallment.setTransaction(transaction);
        newInstallment.setInstallmentNumber(installment.getInstallmentNumber());
        newInstallment.setAmount(installment.getAmount());
        newInstallment.setDueDate(installment.getDueDate());
        newInstallment.setIsPaid(installment.getIsPaid());

        BNPLInstallment saved = installmentRepository.save(newInstallment);
        log.info("Installment created with ID: {}", saved.getId());
        return mapToResponseDTO(saved);
    }

    /**
     * Updates an existing BNPL installment with provided details.
     * Validates the installment and optional transaction ID.
     * @param id ID of the installment to update.
     * @param updated dto containing updated installment details.
     * @return BNPLInstallmentResponseDTO with updated installment details.
     * @throws ResourceNotFoundException if installment or transaction is not found.
     * @throws BadRequestException if update data is invalid.
     */
    @Override
    @Transactional
    public BNPLInstallmentResponseDTO updateInstallment(Long id, BNPLInstallmentUpdateDTO updated) {
        log.info("Updating installment with ID: {}", id);
        BNPLInstallment existing = installmentRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Installment not found for ID: {}", id);
                    return new ResourceNotFoundException("Installment not found");
                });

        if (updated.getTransactionId() != null) {
            Transaction transaction = transactionRepository.findById(updated.getTransactionId())
                    .orElseThrow(() -> {
                        log.error("Transaction not found for ID: {}", updated.getTransactionId());
                        return new ResourceNotFoundException("Transaction not found");
                    });
            existing.setTransaction(transaction);
            log.debug("Updated transaction ID for installment ID: {} to {}", id, updated.getTransactionId());
        }
        if (updated.getInstallmentNumber() != null) {
            existing.setInstallmentNumber(updated.getInstallmentNumber());
            log.debug("Updated installment number for installment ID: {} to {}", id, updated.getInstallmentNumber());
        }
        existing.setAmount(updated.getAmount());
        existing.setDueDate(updated.getDueDate());
        if (updated.getIsPaid() != null) {
            existing.setIsPaid(updated.getIsPaid());
            log.debug("Updated payment status for installment ID: {} to {}", id, updated.getIsPaid());
        }

        BNPLInstallment saved = installmentRepository.save(existing);
        log.info("Installment updated with ID: {}", saved.getId());
        return mapToResponseDTO(saved);
    }

    /**
     * Deletes an existing BNPL installment.
     * @param id ID of the installment to delete.
     * @throws ResourceNotFoundException if installment is not found.
     */
    @Override
    @Transactional
    public void deleteInstallment(Long id) {
        log.info("Deleting installment with ID: {}", id);
        BNPLInstallment existing = installmentRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Installment not found for ID: {}", id);
                    return new ResourceNotFoundException("Installment not found");
                });
        installmentRepository.delete(existing);
        log.info("Installment deleted with ID: {}", id);
    }

    /**
     * Retrieves all installments for a specific transaction.
     * @param transactionId ID of the transaction to fetch installments for.
     * @return List of BNPLInstallmentResponseDTOs representing all installments for the transaction.
     */
    @Override
    public List<BNPLInstallmentResponseDTO> getAllInstallmentsByTransactionId(Long transactionId) {
        log.info("Fetching all installments for transaction ID: {}", transactionId);
        List<BNPLInstallment> installments = installmentRepository.findByTransactionId(transactionId);
        log.debug("Found {} installments for transaction ID: {}", installments.size(), transactionId);
        return installments.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Maps a BNPLInstallment entity to a BNPLInstallmentResponseDTO for frontend response.
     * Computes late fee using LateFeeCalculatorService.
     * @param installment BNPLInstallment entity to map.
     * @return BNPLInstallmentResponseDTO with relevant fields.
     */
    private BNPLInstallmentResponseDTO mapToResponseDTO(BNPLInstallment installment) {
        log.debug("Mapping installment ID: {} to response dto", installment.getId());
        BNPLInstallmentResponseDTO dto = new BNPLInstallmentResponseDTO();
        dto.setId(installment.getId());
        dto.setTransactionId(installment.getTransaction().getId());
        dto.setInstallmentNumber(installment.getInstallmentNumber());
        dto.setAmount(installment.getAmount());
        dto.setDueDate(installment.getDueDate());
        dto.setIsPaid(installment.getIsPaid());
        dto.setLateFee(lateFeeCalculatorService.calculateLateFeeForInstallment(installment));
        log.debug("Late fee for installment ID: {} set to {}", installment.getId(), dto.getLateFee());
        return dto;
    }
}