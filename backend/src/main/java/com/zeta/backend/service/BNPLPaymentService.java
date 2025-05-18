package com.zeta.backend.service;

import com.zeta.backend.exception.ResourceNotFoundException;
import com.zeta.backend.model.BNPLInstallment;
import com.zeta.backend.model.Card;
import com.zeta.backend.model.Transaction;
import com.zeta.backend.repository.BNPLInstallmentRepository;
import com.zeta.backend.repository.CardRepository;
import com.zeta.backend.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BNPLPaymentService implements IBNPLPaymentService {

    private final BNPLInstallmentRepository bnplInstallmentRepository;
    private final TransactionRepository transactionRepository;
    private final CardRepository cardRepository;

    @Override
    @Transactional
    public void payInstallment(Long installmentId, Double amount) {
        if (amount == null || amount <= 0) {
            throw new IllegalArgumentException("Payment amount must be positive");
        }

        BNPLInstallment installment = bnplInstallmentRepository.findById(installmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Installment not found"));

        if (Boolean.TRUE.equals(installment.getIsPaid())) {
            throw new IllegalStateException("Installment already paid");
        }

        if (!amount.equals(installment.getAmount())) {
            throw new IllegalArgumentException(String.format(
                    "Payment amount %.2f must equal the installment amount %.2f",
                    amount, installment.getAmount()));
        }

        // Mark installment paid
        installment.setIsPaid(true);
        bnplInstallmentRepository.save(installment);

        // Refund amount back to card available limit
        Transaction transaction = installment.getTransaction();
        Card card = transaction.getCard();
        card.setAvailableLimit(card.getAvailableLimit() + amount);
        cardRepository.save(card);
    }

    @Override
    public List<BNPLInstallment> getPendingInstallmentsByTransactionId(Long transactionId) {
        return bnplInstallmentRepository.findByTransactionIdAndIsPaidFalse(transactionId);
    }

    @Override
    public List<BNPLInstallment> getOverdueInstallmentsByCardId(Long cardId) {
        LocalDate today = LocalDate.now();
        return bnplInstallmentRepository.findOverdueByCardId(cardId, today);
    }
}
