package com.zeta.backend.service;

import com.zeta.backend.model.BNPLInstallment;

import java.util.List;

public interface IBNPLPaymentService {
    void payInstallment(Long installmentId, Double amount);

    List<BNPLInstallment> getPendingInstallmentsByTransactionId(Long transactionId);

    List<BNPLInstallment> getOverdueInstallmentsByCardId(Long cardId);

    // CRUD methods
    List<BNPLInstallment> getAllInstallments();

    BNPLInstallment getInstallmentById(Long id);

    BNPLInstallment createInstallment(BNPLInstallment installment);

    BNPLInstallment updateInstallment(Long id, BNPLInstallment updated);

    void deleteInstallment(Long id);
}
