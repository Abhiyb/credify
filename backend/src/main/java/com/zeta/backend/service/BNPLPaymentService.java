package com.zeta.backend.service;

import com.zeta.backend.exception.ResourceNotFoundException;
import com.zeta.backend.model.BNPLInstallment;
import com.zeta.backend.repository.BNPLInstallmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BNPLPaymentService implements IBNPLPaymentService {

    private final BNPLInstallmentRepository repository;

    /**
     * Marks a BNPL installment as paid if the amount matches.
     */
    @Override
    public void payInstallment(Long installmentId, Double amount) {
        BNPLInstallment installment = repository.findById(installmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Installment not found"));

        if (amount == null || !installment.getAmount().equals(amount)) {
            throw new IllegalArgumentException("Amount mismatch or null");
        }

        installment.setIsPaid(true);
        repository.save(installment);
        log.info("Installment with ID {} has been marked as paid", installmentId);
    }

    /**
     * Returns all pending (unpaid) installments for a given transaction.
     */
    @Override
    public List<BNPLInstallment> getPendingInstallmentsByTransactionId(Long transactionId) {
        log.info("Fetching pending installments for transaction ID {}", transactionId);
        return repository.findByTransaction_IdAndIsPaidFalse(transactionId);
    }

    /**
     * Returns all overdue (past due date) and unpaid installments for a given card.
     */
    @Override
    public List<BNPLInstallment> getOverdueInstallmentsByCardId(Long cardId) {
        log.info("Fetching overdue installments for card ID {}", cardId);
        return repository.findByTransaction_Card_CardIdAndIsPaidFalseAndDueDateBefore(cardId, LocalDate.now());
    }

    /**
     * Returns all installments (paid and unpaid) for a given transaction.
     */
    @Override
    public List<BNPLInstallment> getAllInstallmentsByTransactionId(Long transactionId) {
        log.info("Fetching all installments for transaction ID {}", transactionId);
        return repository.findByTransactionId(transactionId);
    }

    /**
     * Returns all BNPL installments in the system.
     */
    @Override
    public List<BNPLInstallment> getAllInstallments() {
        log.info("Fetching all BNPL installments");
        return repository.findAll();
    }

    /**
     * Fetches a specific BNPL installment by ID.
     */
    @Override
    public BNPLInstallment getInstallmentById(Long id) {
        log.info("Fetching installment with ID {}", id);
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Installment not found"));
    }

    /**
     * Creates a new BNPL installment record.
     */
    @Override
    public BNPLInstallment createInstallment(BNPLInstallment installment) {
        log.info("Creating new BNPL installment");
        return repository.save(installment);
    }

    /**
     * Updates an existing BNPL installment with new details.
     */
    @Override
    public BNPLInstallment updateInstallment(Long id, BNPLInstallment updated) {
        BNPLInstallment existing = getInstallmentById(id);

        existing.setAmount(updated.getAmount());
        existing.setDueDate(updated.getDueDate());
        existing.setIsPaid(updated.getIsPaid());
        existing.setInstallmentNumber(updated.getInstallmentNumber());
        existing.setTransaction(updated.getTransaction());

        log.info("Updating installment with ID {}", id);
        return repository.save(existing);
    }

    /**
     * Deletes a BNPL installment by ID.
     */
    @Override
    public void deleteInstallment(Long id) {
        BNPLInstallment existing = getInstallmentById(id);
        repository.delete(existing);
        log.info("Deleted installment with ID {}", id);
    }
}
