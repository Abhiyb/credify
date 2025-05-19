package com.zeta.backend.service;

import com.zeta.backend.exception.ResourceNotFoundException;
import com.zeta.backend.model.BNPLInstallment;
import com.zeta.backend.repository.BNPLInstallmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BNPLPaymentService implements IBNPLPaymentService {

    private final BNPLInstallmentRepository repository;

    @Override
    public void payInstallment(Long installmentId, Double amount) {
        BNPLInstallment installment = repository.findById(installmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Installment not found"));
        if (!installment.getAmount().equals(amount)) {
            throw new IllegalArgumentException("Amount mismatch");
        }
        installment.setIsPaid(true);
        repository.save(installment);
    }

    @Override
    public List<BNPLInstallment> getPendingInstallmentsByTransactionId(Long transactionId) {
        return repository.findByTransaction_IdAndIsPaidFalse(transactionId);
    }

    @Override
    public List<BNPLInstallment> getOverdueInstallmentsByCardId(Long cardId) {
        return repository.findByTransaction_Card_CardIdAndIsPaidFalseAndDueDateBefore(cardId, LocalDate.now());
    }

    // 🔁 CRUD Implementation

    @Override
    public List<BNPLInstallment> getAllInstallments() {
        return repository.findAll();
    }

    @Override
    public BNPLInstallment getInstallmentById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Installment not found"));
    }

    @Override
    public BNPLInstallment createInstallment(BNPLInstallment installment) {
        return repository.save(installment);
    }

    @Override
    public BNPLInstallment updateInstallment(Long id, BNPLInstallment updated) {
        BNPLInstallment existing = getInstallmentById(id);
        existing.setAmount(updated.getAmount());
        existing.setDueDate(updated.getDueDate());
        existing.setIsPaid(updated.getIsPaid());
        existing.setInstallmentNumber(updated.getInstallmentNumber());
        existing.setTransaction(updated.getTransaction());
        return repository.save(existing);
    }

    @Override
    public void deleteInstallment(Long id) {
        BNPLInstallment existing = getInstallmentById(id);
        repository.delete(existing);
    }
}
