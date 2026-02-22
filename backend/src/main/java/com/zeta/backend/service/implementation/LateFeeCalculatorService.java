package com.zeta.backend.service.implementation;

import com.zeta.backend.model.BNPLInstallment;
import com.zeta.backend.repository.BNPLInstallmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class LateFeeCalculatorService {

    private final BNPLInstallmentRepository bnplInstallmentRepository;

    // Configuration for late fee
    private static final double DAILY_LATE_FEE_FLAT = 1.0; // $1 per day
    private static final double DAILY_LATE_FEE_PERCENTAGE = 0.005; // 0.5% per day of amount

    /**
     * Calculates total late fee for all overdue installments linked to a specific card.
     */
    public double calculateTotalLateFeeByCardId(Long cardId) {
        LocalDate today = LocalDate.now();
        log.info("Calculating total late fee for card ID {} as of {}", cardId, today);

        List<BNPLInstallment> overdueInstallments =
                bnplInstallmentRepository.findOverdueByCardId(cardId, today);

        double totalFee = overdueInstallments.stream()
                .mapToDouble(this::calculateLateFeeForInstallment)
                .sum();

        log.info("Total late fee for card ID {} is {}", cardId, totalFee);
        return totalFee;
    }

    /**
     * Calculates late fee for a single overdue installment.
     */
    public double calculateLateFeeForInstallment(BNPLInstallment installment) {
        if (Boolean.TRUE.equals(installment.getIsPaid())) {
            log.debug("Installment ID {} is already paid. No late fee.", installment.getId());
            return 0.0;
        }

        LocalDate today = LocalDate.now();
        LocalDate dueDate = installment.getDueDate();

        if (dueDate == null || !dueDate.isBefore(today)) {
            log.debug("Installment ID {} is not overdue. No late fee.", installment.getId());
            return 0.0;
        }

        long daysLate = ChronoUnit.DAYS.between(dueDate, today);
        double baseAmount = installment.getAmount();

        // Apply flat and percentage-based late fee
        double fee = (DAILY_LATE_FEE_FLAT * daysLate) +
                (baseAmount * DAILY_LATE_FEE_PERCENTAGE * daysLate);

        double roundedFee = Math.round(fee * 100.0) / 100.0;

        log.info("Installment ID {} is {} days late. Calculated late fee: {}",
                installment.getId(), daysLate, roundedFee);

        return roundedFee;
    }
}
