package com.zeta.backend.controller;

import com.zeta.backend.dto.TransactionCreateDTO;
import com.zeta.backend.dto.TransactionResponseDTO;
import com.zeta.backend.dto.TransactionUpdateDTO;
import com.zeta.backend.enums.InstallmentPlan;
import com.zeta.backend.exception.BadRequestException;
import com.zeta.backend.service.ITransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing transaction-related API endpoints.
 * Handles regular and BNPL transaction creation, retrieval, updates, and deletion.
 * Uses DTOs for secure data transfer and logging for monitoring.
 */
@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "http://localhost:3000")
public class TransactionController {

    private final ITransactionService transactionService;

    /**
     * Creates a regular (non-BNPL) transaction.
     * @param transaction dto containing transaction details.
     * @return ResponseEntity with TransactionResponseDTO.
     * @throws BadRequestException if dto validation fails.
     */
    @PostMapping
    public ResponseEntity<TransactionResponseDTO> createRegularTransaction(
            @Valid @RequestBody TransactionCreateDTO transaction) {
        log.info("Received request to create regular transaction for card ID: {}", transaction.getCardId());
        TransactionResponseDTO response = transactionService.simulateRegularTransaction(transaction);
        log.info("Created regular transaction with ID: {}", response.getId());
        return ResponseEntity.ok(response);
    }

    /**
     * Creates a BNPL transaction with the specified installment plan.
     * @param transaction dto containing transaction details.
     * @param plan Installment plan (THREE, SIX, or NINE months).
     * @return ResponseEntity with TransactionResponseDTO.
     * @throws BadRequestException if dto validation fails or plan is invalid.
     */
    @PostMapping("/bnpl")
    public ResponseEntity<TransactionResponseDTO> createBNPLTransaction(
            @Valid @RequestBody TransactionCreateDTO transaction,
            @RequestParam InstallmentPlan plan) {
        log.info("Received request to create BNPL transaction for card ID: {} with plan: {}",
                transaction.getCardId(), plan);
        TransactionResponseDTO response = transactionService.simulateBNPLTransaction(transaction, plan);
        log.info("Created BNPL transaction with ID: {}", response.getId());
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves transaction history for a specific card.
     * @param cardId ID of the card to fetch transactions for.
     * @return ResponseEntity with List of TransactionResponseDTOs.
     */
    @GetMapping("/card/{cardId}")
    public ResponseEntity<List<TransactionResponseDTO>> getTransactionHistory(
            @PathVariable Long cardId) {
        log.info("Received request to fetch transaction history for card ID: {}", cardId);
        List<TransactionResponseDTO> transactions = transactionService.getTransactionHistoryByCardId(cardId);
        log.debug("Returning {} transactions for card ID: {}", transactions.size(), cardId);
        return ResponseEntity.ok(transactions);
    }

    /**
     * Retrieves all transactions in the system.
     * @return ResponseEntity with List of TransactionResponseDTOs.
     */
    @GetMapping
    public ResponseEntity<List<TransactionResponseDTO>> getAllTransactions() {
        log.info("Received request to fetch all transactions");
        List<TransactionResponseDTO> transactions = transactionService.getAllTransactions();
        log.debug("Returning {} transactions", transactions.size());
        return ResponseEntity.ok(transactions);
    }

    /**
     * Retrieves a transaction by its ID.
     * @param id ID of the transaction to fetch.
     * @return ResponseEntity with TransactionResponseDTO.
     */
    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponseDTO> getTransactionById(@PathVariable Long id) {
        log.info("Received request to fetch transaction with ID: {}", id);
        TransactionResponseDTO transaction = transactionService.getTransactionById(id);
        log.debug("Returning transaction with ID: {}", id);
        return ResponseEntity.ok(transaction);
    }

    /**
     * Updates an existing transaction.
     * @param id ID of the transaction to update.
     * @param transaction dto containing updated transaction details.
     * @return ResponseEntity with TransactionResponseDTO.
     * @throws BadRequestException if dto validation fails.
     */
    @PutMapping("/{id}")
    public ResponseEntity<TransactionResponseDTO> updateTransaction(
            @PathVariable Long id,
            @Valid @RequestBody TransactionUpdateDTO transaction) {
        log.info("Received request to update transaction with ID: {}", id);
        TransactionResponseDTO response = transactionService.updateTransaction(id, transaction);
        log.info("Updated transaction with ID: {}", id);
        return ResponseEntity.ok(response);
    }

    /**
     * Deletes a transaction by its ID.
     * @param id ID of the transaction to delete.
     * @return ResponseEntity with no content.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long id) {
        log.info("Received request to delete transaction with ID: {}", id);
        transactionService.deleteTransaction(id);
        log.info("Deleted transaction with ID: {}", id);
        return ResponseEntity.noContent().build();
    }
}