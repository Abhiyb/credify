package com.zeta.backend.repository;

import com.zeta.backend.model.BNPLInstallment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for BNPLInstallment entity.
 */
@Repository
public interface BNPLInstallmentRepository extends JpaRepository<BNPLInstallment, Long> {

    List<BNPLInstallment> findByTransactionId(Long transactionId);

    @Query("SELECT b FROM BNPLInstallment b WHERE b.transaction.id = :transactionId AND b.isPaid = false ORDER BY b.installmentNumber ASC")
    List<BNPLInstallment> getNextUnpaidInstallment(@Param("transactionId") Long transactionId);

    @Query("SELECT b FROM BNPLInstallment b WHERE b.transaction.id = :transactionId AND b.isPaid = false")
    List<BNPLInstallment> findUnpaidByTransactionId(@Param("transactionId") Long transactionId);

    @Query("SELECT b FROM BNPLInstallment b WHERE b.transaction.card.cardId = :cardId AND b.isPaid = false AND b.dueDate < :today")
    List<BNPLInstallment> findOverdueByCardId(@Param("cardId") Long cardId, @Param("today") LocalDate today);

    List<BNPLInstallment> findByTransaction_IdAndIsPaidFalse(Long transactionId);

    List<BNPLInstallment> findByTransaction_Card_CardIdAndIsPaidFalseAndDueDateBefore(Long cardId, LocalDate date);

    /**
     * New query to get the email of the user who owns a card.
     * Needed for JWT authorization checks in LateFeeController.
     */
    @Query("SELECT b.transaction.card.user.email FROM BNPLInstallment b WHERE b.transaction.card.cardId = :cardId")
    Optional<String> findCardOwnerEmailByCardId(@Param("cardId") Long cardId);
}
