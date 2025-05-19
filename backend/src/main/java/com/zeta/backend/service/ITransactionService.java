package com.zeta.backend.service;

import com.zeta.backend.enums.InstallmentPlan;
import com.zeta.backend.model.Transaction;

import java.util.List;

public interface ITransactionService {

    Transaction simulateRegularTransaction(Transaction transaction);

    Transaction simulateBNPLTransaction(Transaction transaction, InstallmentPlan plan);

    List<Transaction> getTransactionHistoryByCardId(Long cardId);

    List<Transaction> getAllTransactions();

    Transaction getTransactionById(Long id);

    Transaction updateTransaction(Long id, Transaction updatedTransaction);

    void deleteTransaction(Long id);
}
