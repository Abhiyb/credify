package com.zeta.backend.service;

import com.zeta.backend.model.BNPLInstallment;
import com.zeta.backend.repository.BNPLInstallmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LateFeeCalculator {

    private final BNPLInstallmentRepository bnplInstallmentRepository;

    // Fee configuration: e.g. $1 per day late or 0.5% per day of installment amount
    private static final double DAILY_LATE_FEE_FLAT = 1.0;
    private static final double DAILY_LATE_FEE_PERCENTAGE = 0.005; // 0.5% per day

    /**
     * Calculate total late fee for all overdue installments of a card
     */
    public double calculateTotalLateFeeByCardId(Long cardId) {
        LocalDate today = LocalDate.now();
        List<BNPLInstallment> overdueInstallments = bnplInstallmentRepository.findOverdueByCardId(cardId, today);

        return overdueInstallments.stream()
                .mapToDouble(this::calculateLateFeeForInstallment)
                .sum();
    }

    /**
     * Calculate late fee for one installment
     */
    public double calculateLateFeeForInstallment(BNPLInstallment installment) {
        if (Boolean.TRUE.equals(installment.getIsPaid())) {
            return 0.0;
        }

        LocalDate today = LocalDate.now();
        LocalDate dueDate = installment.getDueDate();

        if (dueDate == null || !dueDate.isBefore(today)) {
            return 0.0;  // not overdue
        }

        long daysLate = ChronoUnit.DAYS.between(dueDate, today);
        double baseAmount = installment.getAmount();

        // Flat fee + percentage fee example
        double fee = (DAILY_LATE_FEE_FLAT * daysLate) + (baseAmount * DAILY_LATE_FEE_PERCENTAGE * daysLate);
        return Math.round(fee * 100.0) / 100.0;  // round to 2 decimals
    }
}
