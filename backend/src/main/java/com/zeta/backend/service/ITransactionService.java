package com.zeta.backend.service;

import com.zeta.backend.model.Transaction;

import java.util.List;

public interface ITransactionService {
    Transaction simulateTransaction(Transaction transaction, int installmentCount);
    List<Transaction> getTransactionHistoryByCardId(Long cardId);
}
