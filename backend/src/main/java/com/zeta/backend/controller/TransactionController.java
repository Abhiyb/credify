package com.zeta.backend.controller;

import com.zeta.backend.dto.TransactionCreateDTO;
import com.zeta.backend.dto.TransactionResponseDTO;
import com.zeta.backend.dto.TransactionUpdateDTO;
import com.zeta.backend.enums.InstallmentPlan;
import com.zeta.backend.security.JwtUtil;
import com.zeta.backend.service.ITransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
@Slf4j
public class TransactionController {

    private final ITransactionService transactionService;
    private final JwtUtil jwtUtil;

    /**
     * NEW: Validate card details before proceeding (called by frontend checkEligibility)
     */
    @PostMapping("/validate-card")
    public ResponseEntity<Map<String, Object>> validateCard(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody TransactionCreateDTO dto) {

        String userEmail = jwtUtil.extractEmail(extractToken(token));
        log.info("Validating card ending in ****{} for user {}",
                dto.getCardNumber().substring(dto.getCardNumber().length() - 4), userEmail);

        Map<String, Object> response = new HashMap<>();

        try {
            boolean isValid = transactionService.validateCardDetails(dto, userEmail);

            response.put("eligible", isValid);
            response.put("message", isValid ? "Card validated successfully" : "Invalid card details or not found");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Card validation error", e);
            response.put("eligible", false);
            response.put("message", e.getMessage() != null ? e.getMessage() : "Card validation failed");
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Create a regular (non-BNPL) transaction
     */
    @PostMapping
    public ResponseEntity<TransactionResponseDTO> createRegularTransaction(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody TransactionCreateDTO dto) {

        String userEmail = jwtUtil.extractEmail(extractToken(token));
        log.info("Creating regular transaction for card ending in ****{} by user {}",
                dto.getCardNumber().substring(dto.getCardNumber().length() - 4), userEmail);

        TransactionResponseDTO response = transactionService.simulateRegularTransaction(dto, userEmail);
        return ResponseEntity.ok(response);
    }

    /**
     * Create a BNPL transaction
     */
    @PostMapping("/bnpl")
    public ResponseEntity<TransactionResponseDTO> createBNPLTransaction(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody TransactionCreateDTO dto,
            @RequestParam InstallmentPlan plan) {

        String userEmail = jwtUtil.extractEmail(extractToken(token));
        log.info("Creating BNPL transaction for card ending in ****{} by user {} with plan: {}",
                dto.getCardNumber().substring(dto.getCardNumber().length() - 4), userEmail, plan);

        TransactionResponseDTO response = transactionService.simulateBNPLTransaction(dto, plan, userEmail);
        return ResponseEntity.ok(response);
    }

    /**
     * Get transaction history by card
     */
    @GetMapping("/card/{cardId}")
    public ResponseEntity<List<TransactionResponseDTO>> getTransactionHistory(
            @RequestHeader("Authorization") String token,
            @PathVariable Long cardId) {

        String userEmail = jwtUtil.extractEmail(extractToken(token));
        log.info("Fetching transaction history for card ID: {} by user {}", cardId, userEmail);

        List<TransactionResponseDTO> transactions = transactionService.getTransactionHistoryByCardId(cardId, userEmail);
        return ResponseEntity.ok(transactions);
    }

    /**
     * Get all transactions (admin or general view)
     */
    @GetMapping
    public ResponseEntity<List<TransactionResponseDTO>> getAllTransactions() {
        log.info("Fetching all transactions");
        List<TransactionResponseDTO> transactions = transactionService.getAllTransactions();
        return ResponseEntity.ok(transactions);
    }

    /**
     * Get transaction by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponseDTO> getTransactionById(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id) {

        String userEmail = jwtUtil.extractEmail(extractToken(token));
        log.info("Fetching transaction ID: {} by user {}", id, userEmail);

        TransactionResponseDTO transaction = transactionService.getTransactionById(id, userEmail);
        return ResponseEntity.ok(transaction);
    }

    /**
     * Update transaction
     */
    @PutMapping("/{id}")
    public ResponseEntity<TransactionResponseDTO> updateTransaction(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id,
            @Valid @RequestBody TransactionUpdateDTO transaction) {

        String userEmail = jwtUtil.extractEmail(extractToken(token));
        log.info("Updating transaction ID: {} by user {}", id, userEmail);

        TransactionResponseDTO response = transactionService.updateTransaction(id, transaction, userEmail);
        return ResponseEntity.ok(response);
    }

    /**
     * Delete transaction
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id) {

        String userEmail = jwtUtil.extractEmail(extractToken(token));
        log.info("Deleting transaction ID: {} by user {}", id, userEmail);

        transactionService.deleteTransaction(id, userEmail);
        return ResponseEntity.noContent().build();
    }

    /**
     * Extracts JWT token from Authorization header
     */
    private String extractToken(String authHeader) {
        if (authHeader == null || authHeader.isEmpty()) {
            throw new IllegalArgumentException("Authorization header missing");
        }
        if (authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return authHeader;
    }
}