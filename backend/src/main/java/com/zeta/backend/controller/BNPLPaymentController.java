package com.zeta.backend.controller;

import com.zeta.backend.model.BNPLInstallment;
import com.zeta.backend.service.IBNPLPaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/bnpl/installments")
@RequiredArgsConstructor
@Slf4j
public class BNPLPaymentController {

    private final IBNPLPaymentService bnplPaymentService;

    /**
     * Get all installments linked to a transaction
     */
    @GetMapping("/transaction/{transactionId}")
    public ResponseEntity<List<BNPLInstallment>> getAllInstallmentsByTransactionId(@PathVariable Long transactionId) {
        log.info("Fetching all installments for transaction ID {}", transactionId);
        List<BNPLInstallment> installments = bnplPaymentService.getAllInstallmentsByTransactionId(transactionId);
        return ResponseEntity.ok(installments);
    }

    /**
     * Pay a specific installment by ID
     */
    @PostMapping("/{installmentId}/pay")
    public ResponseEntity<String> payInstallment(
            @PathVariable Long installmentId,
            @RequestParam Double amount) {
        log.info("Paying installment ID {} with amount {}", installmentId, amount);
        bnplPaymentService.payInstallment(installmentId, amount);
        return ResponseEntity.ok("Installment paid successfully");
    }

    /**
     * Get all pending installments for a given transaction
     */
    @GetMapping("/pending/{transactionId}")
    public ResponseEntity<List<BNPLInstallment>> getPendingInstallments(@PathVariable Long transactionId) {
        log.info("Fetching pending installments for transaction ID {}", transactionId);
        List<BNPLInstallment> pending = bnplPaymentService.getPendingInstallmentsByTransactionId(transactionId);
        return ResponseEntity.ok(pending);
    }

    /**
     * Get all overdue installments for a given card
     */
    @GetMapping("/overdue/{cardId}")
    public ResponseEntity<List<BNPLInstallment>> getOverdueInstallments(@PathVariable Long cardId) {
        log.info("Fetching overdue installments for card ID {}", cardId);
        List<BNPLInstallment> overdue = bnplPaymentService.getOverdueInstallmentsByCardId(cardId);
        return ResponseEntity.ok(overdue);
    }

    /**
     * Get all BNPL installments
     */
    @GetMapping
    public ResponseEntity<List<BNPLInstallment>> getAllInstallments() {
        log.info("Fetching all BNPL installments");
        return ResponseEntity.ok(bnplPaymentService.getAllInstallments());
    }

    /**
     * Get installment details by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<BNPLInstallment> getInstallmentById(@PathVariable Long id) {
        log.info("Fetching installment by ID {}", id);
        return ResponseEntity.ok(bnplPaymentService.getInstallmentById(id));
    }

    /**
     * Create a new BNPL installment
     */
    @PostMapping
    public ResponseEntity<BNPLInstallment> createInstallment(@RequestBody BNPLInstallment installment) {
        log.info("Creating new BNPL installment");
        return ResponseEntity.ok(bnplPaymentService.createInstallment(installment));
    }

    /**
     * Update an existing BNPL installment
     */
    @PutMapping("/{id}")
    public ResponseEntity<BNPLInstallment> updateInstallment(@PathVariable Long id, @RequestBody BNPLInstallment updated) {
        log.info("Updating installment ID {}", id);
        return ResponseEntity.ok(bnplPaymentService.updateInstallment(id, updated));
    }

    /**
     * Delete a BNPL installment by ID
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteInstallment(@PathVariable Long id) {
        log.info("Deleting installment ID {}", id);
        bnplPaymentService.deleteInstallment(id);
        return ResponseEntity.ok("Installment deleted successfully");
    }
}
