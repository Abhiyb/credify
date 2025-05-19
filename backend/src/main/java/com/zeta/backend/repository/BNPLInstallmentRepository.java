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

    @Query("SELECT b FROM BNPLInstallment b WHERE b.transaction.id = :transactionId")
    List<BNPLInstallment> findByTransactionId(@Param("transactionId") Long transactionId);

    // Use this to fetch unpaid installments by transaction, ordered by installment number
    @Query("SELECT b FROM BNPLInstallment b WHERE b.transaction.id = :transactionId AND b.isPaid = false ORDER BY b.installmentNumber ASC")
    List<BNPLInstallment> getNextUnpaidInstallment(@Param("transactionId") Long transactionId);

    @Query("SELECT b FROM BNPLInstallment b WHERE b.transaction.id = :transactionId AND b.isPaid = false")
    List<BNPLInstallment> findUnpaidByTransactionId(@Param("transactionId") Long transactionId);

    // Find overdue installments by card ID where dueDate is before today and isPaid = false
    @Query("SELECT b FROM BNPLInstallment b WHERE b.transaction.card.id = :cardId AND b.isPaid = false AND b.dueDate < :today")
    List<BNPLInstallment> findOverdueByCardId(@Param("cardId") Long cardId, @Param("today") LocalDate today);

    // Derived query methods matching entity field names:
    List<BNPLInstallment> findByTransaction_IdAndIsPaidFalse(Long transactionId);

    List<BNPLInstallment> findByTransaction_Card_CardIdAndIsPaidFalseAndDueDateBefore(Long cardId, LocalDate date);

}
