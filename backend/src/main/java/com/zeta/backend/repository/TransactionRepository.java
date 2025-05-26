package com.zeta.backend.repository;

import com.zeta.backend.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for managing Transaction entities.
 * Includes derived queries for counting and filtering transactions.
 */
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    // Counts the number of transactions for a specific card
    Long countByCardId(Long cardId);

    // Finds all transactions associated with a specific card
    List<Transaction> findByCardId(Long cardId);

    // Retrieves all transactions that are marked as BNPL (Buy Now Pay Later)
    List<Transaction> findByIsBNPLTrue();
}
