package com.zeta.backend.service;

import com.zeta.backend.dto.TransactionCreateDTO;
import com.zeta.backend.dto.TransactionResponseDTO;
import com.zeta.backend.dto.TransactionUpdateDTO;
import com.zeta.backend.enums.InstallmentPlan;

import java.util.List;

public interface ITransactionService {

    // Simulates a regular transaction
    TransactionResponseDTO simulateRegularTransaction(TransactionCreateDTO transaction, String userEmail);

    // Simulates a BNPL transaction
    TransactionResponseDTO simulateBNPLTransaction(TransactionCreateDTO transaction, InstallmentPlan plan, String userEmail);

    // Get transaction history for a card (checks ownership)
    List<TransactionResponseDTO> getTransactionHistoryByCardId(Long cardId, String userEmail);

    // Get all transactions (admin access usually, no email needed)
    List<TransactionResponseDTO> getAllTransactions();

    // Get a transaction by ID (checks ownership)
    TransactionResponseDTO getTransactionById(Long id, String userEmail);

    // Update a transaction (checks ownership)
    TransactionResponseDTO updateTransaction(Long id, TransactionUpdateDTO updatedTransaction, String userEmail);


    boolean validateCardDetails(TransactionCreateDTO dto, String userEmail);
    // Delete a transaction (checks ownership)
    void deleteTransaction(Long id, String userEmail);
}
