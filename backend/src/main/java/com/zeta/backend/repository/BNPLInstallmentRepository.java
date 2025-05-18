package com.zeta.backend.repository;

import com.zeta.backend.model.BNPLInstallment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BNPLInstallmentRepository extends JpaRepository<BNPLInstallment, Long> {

    List<BNPLInstallment> findByTransactionId(Long transactionId);

    List<BNPLInstallment> findByIsPaidFalse();

    // Use this to fetch unpaid installments by transaction, ordered by installment number
    @Query("SELECT b FROM BNPLInstallment b WHERE b.transaction.id = :transactionId AND b.isPaid = false ORDER BY b.installmentNumber ASC")
    List<BNPLInstallment> getNextUnpaidInstallment(@Param("transactionId") Long transactionId);

    // Fetch unpaid installments by transaction ID (same as above but without ordering)
    List<BNPLInstallment> findByTransactionIdAndIsPaidFalse(Long transactionId);

    // Find overdue installments by card ID where dueDate is before today and isPaid = false
    @Query("SELECT b FROM BNPLInstallment b WHERE b.transaction.card.cardId = :cardId AND b.isPaid = false AND b.dueDate < :today")
    List<BNPLInstallment> findOverdueByCardId(@Param("cardId") Long cardId, @Param("today") LocalDate today);
}
