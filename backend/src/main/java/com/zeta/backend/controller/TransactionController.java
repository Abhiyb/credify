package com.zeta.backend.controller;

import com.zeta.backend.enums.InstallmentPlan;
import com.zeta.backend.exception.ResourceNotFoundException;
import com.zeta.backend.model.Card;
import com.zeta.backend.model.Transaction;
import com.zeta.backend.service.ITransactionService;
import com.zeta.backend.repository.CardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
@Slf4j
public class TransactionController {

    private final ITransactionService transactionService;
    private final CardRepository cardRepository;

    /**
     * Create or simulate a transaction (regular or BNPL)
     */
    @PostMapping
    public ResponseEntity<Transaction> simulateTransaction(
            @RequestBody Transaction transaction,
            @RequestParam(value = "plan", defaultValue = "THREE") InstallmentPlan plan) {

        log.info("Simulating transaction for card ID: {}", transaction.getCardId());

        // Fetch and set card if only cardId is provided
        if (transaction.getCard() == null && transaction.getCardId() != null) {
            Card card = cardRepository.findById(transaction.getCardId())
                    .orElseThrow(() -> {
                        log.error("Card with ID {} not found", transaction.getCardId());
                        return new ResourceNotFoundException("Card not found");
                    });
            transaction.setCard(card);
        }

        Transaction saved;
        if (Boolean.TRUE.equals(transaction.getIsBNPL())) {
            log.info("Processing as BNPL transaction with {}-month plan", plan.name());
            saved = transactionService.simulateBNPLTransaction(transaction, plan);
        } else {
            log.info("Processing as regular transaction");
            saved = transactionService.simulateRegularTransaction(transaction);
        }

        return ResponseEntity.ok(saved);
    }

    /**
     * Get all transactions
     */
    @GetMapping
    public ResponseEntity<List<Transaction>> getAllTransactions() {
        log.info("Fetching all transactions");
        List<Transaction> list = transactionService.getAllTransactions();
        return ResponseEntity.ok(list);
    }

    /**
     * Get a transaction by its ID
     */
    @GetMapping("/id/{id}")
    public ResponseEntity<Transaction> getTransactionById(@PathVariable Long id) {
        log.info("Fetching transaction with ID {}", id);
        Transaction txn = transactionService.getTransactionById(id);
        return ResponseEntity.ok(txn);
    }

    /**
     * Get all transactions for a specific card ID
     */
    @GetMapping("/card/{cardId}")
    public ResponseEntity<List<Transaction>> getTransactionsByCardId(@PathVariable Long cardId) {
        log.info("Fetching transactions for card ID {}", cardId);
        List<Transaction> transactions = transactionService.getTransactionHistoryByCardId(cardId);
        return ResponseEntity.ok(transactions);
    }

    /**
     * Update an existing transaction
     */
    @PutMapping("/id/{id}")
    public ResponseEntity<Transaction> updateTransaction(
            @PathVariable Long id,
            @RequestBody Transaction transaction) {
        log.info("Updating transaction ID {}", id);
        Transaction updated = transactionService.updateTransaction(id, transaction);
        return ResponseEntity.ok(updated);
    }

    /**
     * Delete a transaction by ID
     */
    @DeleteMapping("/id/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long id) {
        log.info("Deleting transaction with ID {}", id);
        transactionService.deleteTransaction(id);
        return ResponseEntity.noContent().build();
    }
}
