package com.zeta.backend.controller;

import com.zeta.backend.enums.InstallmentPlan;
import com.zeta.backend.exception.ResourceNotFoundException;
import com.zeta.backend.model.Card;
import com.zeta.backend.model.Transaction;
import com.zeta.backend.service.ITransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
@CrossOrigin("http://localhost:5173/")

public class TransactionController {

    private final ITransactionService transactionService;
    private final com.zeta.backend.repository.CardRepository cardRepository;

    // Create or simulate transaction (regular or BNPL)

    @PostMapping

    public ResponseEntity<Transaction> simulateTransaction(
            @RequestBody Transaction transaction,
            @RequestParam(value = "plan", defaultValue = "THREE") InstallmentPlan plan) {

        if (transaction.getCard() == null && transaction.getCardId() != null) {
            Card card = cardRepository.findById(transaction.getCardId())
                    .orElseThrow(() -> new ResourceNotFoundException("Card not found"));
            transaction.setCard(card);
        }

        Transaction saved;
        if (Boolean.TRUE.equals(transaction.getIsBNPL())) {
            saved = transactionService.simulateBNPLTransaction(transaction, plan);
        } else {
            saved = transactionService.simulateRegularTransaction(transaction);
        }
        return ResponseEntity.ok(saved);
    }


    // Read all transactions
    @GetMapping
    public ResponseEntity<List<Transaction>> getAllTransactions() {
        List<Transaction> list = transactionService.getAllTransactions();
        return ResponseEntity.ok(list);
    }

    // Read one transaction by its ID
    @GetMapping("/id/{id}")
    public ResponseEntity<Transaction> getTransactionById(@PathVariable Long id) {
        Transaction txn = transactionService.getTransactionById(id);
        return ResponseEntity.ok(txn);
    }

    // Read transactions by card ID
    @GetMapping("/card/{cardId}")
    public ResponseEntity<List<Transaction>> getTransactionsByCardId(@PathVariable Long cardId) {
        List<Transaction> transactions = transactionService.getTransactionHistoryByCardId(cardId);
        return ResponseEntity.ok(transactions);
    }

    // Update an existing transaction
    @PutMapping("/id/{id}")
    public ResponseEntity<Transaction> updateTransaction(
            @PathVariable Long id,
            @RequestBody Transaction transaction) {
        Transaction updated = transactionService.updateTransaction(id, transaction);
        return ResponseEntity.ok(updated);
    }

    // Delete a transaction
    @DeleteMapping("/id/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long id) {
        transactionService.deleteTransaction(id);
        return ResponseEntity.noContent().build();
    }
}
