package com.zeta.backend.repository;

import com.zeta.backend.model.BNPLInstallment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository interface for BNPLInstallment entity.
 */
@Repository
public interface BNPLInstallmentRepository extends JpaRepository<BNPLInstallment, Long> {

    /**
     * Fetches all installments by a transaction ID.
     *
     * @param transactionId the transaction ID
     * @return list of installments
     */
    List<BNPLInstallment> findByTransactionId(Long transactionId);

    /**
     * Custom query to get unpaid installments ordered by installment number (ascending).
     *
     * @param transactionId the transaction ID
     * @return list of unpaid installments
     */
    @Query("SELECT b FROM BNPLInstallment b WHERE b.transaction.id = :transactionId AND b.isPaid = false ORDER BY b.installmentNumber ASC")
    List<BNPLInstallment> getNextUnpaidInstallment(Long transactionId);

    /**
     * Retrieves unpaid installments for a given transaction.
     *
     * @param transactionId the transaction ID
     * @return list of unpaid installments
     */
    @Query("SELECT b FROM BNPLInstallment b WHERE b.transaction.id = :transactionId AND b.isPaid = false")
    List<BNPLInstallment> findUnpaidByTransactionId(Long transactionId);

    /**
     * Finds overdue unpaid installments for a given card.
     *
     * @param cardId   the card ID
     * @param today    the current date to compare due dates
     * @return list of overdue installments
     */
    @Query("SELECT b FROM BNPLInstallment b WHERE b.transaction.card.cardId = :cardId AND b.isPaid = false AND b.dueDate < :today")
    List<BNPLInstallment> findOverdueByCardId(Long cardId, LocalDate today);

    /**
     * Fetches unpaid installments using a derived query method.
     *
     * @param transactionId the transaction ID
     * @return list of unpaid installments
     */
    List<BNPLInstallment> findByTransaction_IdAndIsPaidFalse(Long transactionId);

    /**
     * Finds overdue unpaid installments using nested conditions.
     *
     * @param cardId the card ID
     * @param date   the reference date (usually today)
     * @return list of overdue unpaid installments
     */
    List<BNPLInstallment> findByTransaction_Card_CardIdAndIsPaidFalseAndDueDateBefore(Long cardId, LocalDate date);
}
