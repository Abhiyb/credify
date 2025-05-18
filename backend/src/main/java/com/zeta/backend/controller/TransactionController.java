package com.zeta.backend.controller;

import com.zeta.backend.model.Transaction;
import com.zeta.backend.service.ITransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final ITransactionService transactionService;

    @PostMapping
    public ResponseEntity<Transaction> simulateTransaction(
            @RequestBody Transaction transaction,
            @RequestParam(defaultValue = "1") int installmentCount) {
        Transaction saved = transactionService.simulateTransaction(transaction, installmentCount);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/{cardId}")
    public ResponseEntity<List<Transaction>> getTransactionsByCardId(@PathVariable Long cardId) {
        List<Transaction> transactions = transactionService.getTransactionHistoryByCardId(cardId);
        return ResponseEntity.ok(transactions);
    }
}
