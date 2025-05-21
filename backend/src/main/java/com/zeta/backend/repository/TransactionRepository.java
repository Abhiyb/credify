package com.zeta.backend.repository;

import com.zeta.backend.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Long countByCardId(Long cardId);

    List<Transaction> findByCardId(Long cardId);

    List<Transaction> findByIsBNPLTrue();
}
