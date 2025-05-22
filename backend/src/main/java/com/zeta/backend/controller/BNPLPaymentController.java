package com.zeta.backend.controller;

import com.zeta.backend.model.BNPLInstallment;
import com.zeta.backend.service.IBNPLPaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bnpl/installments")
@RequiredArgsConstructor
public class BNPLPaymentController {

    private final IBNPLPaymentService bnplPaymentService;
    @GetMapping("/transaction/{transactionId}")
    public ResponseEntity<List<BNPLInstallment>> getAllInstallmentsByTransactionId(@PathVariable Long transactionId) {
        List<BNPLInstallment> installments = bnplPaymentService.getAllInstallmentsByTransactionId(transactionId);
        return ResponseEntity.ok(installments);
    }





    @PostMapping("/{installmentId}/pay")
    public ResponseEntity<String> payInstallment(
            @PathVariable Long installmentId,
            @RequestParam Double amount) {
        bnplPaymentService.payInstallment(installmentId, amount);
        return ResponseEntity.ok("Installment paid successfully");
    }

    @GetMapping("/pending/{transactionId}")
    public ResponseEntity<List<BNPLInstallment>> getPendingInstallments(@PathVariable Long transactionId) {
        List<BNPLInstallment> pending = bnplPaymentService.getPendingInstallmentsByTransactionId(transactionId);
        return ResponseEntity.ok(pending);
    }

    @GetMapping("/overdue/{cardId}")
    public ResponseEntity<List<BNPLInstallment>> getOverdueInstallments(@PathVariable Long cardId) {
        List<BNPLInstallment> overdue = bnplPaymentService.getOverdueInstallmentsByCardId(cardId);
        return ResponseEntity.ok(overdue);
    }


    @GetMapping
    public ResponseEntity<List<BNPLInstallment>> getAllInstallments() {
        return ResponseEntity.ok(bnplPaymentService.getAllInstallments());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BNPLInstallment> getInstallmentById(@PathVariable Long id) {
        return ResponseEntity.ok(bnplPaymentService.getInstallmentById(id));
    }

    @PostMapping
    public ResponseEntity<BNPLInstallment> createInstallment(@RequestBody BNPLInstallment installment) {
        return ResponseEntity.ok(bnplPaymentService.createInstallment(installment));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BNPLInstallment> updateInstallment(@PathVariable Long id, @RequestBody BNPLInstallment updated) {
        return ResponseEntity.ok(bnplPaymentService.updateInstallment(id, updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteInstallment(@PathVariable Long id) {
        bnplPaymentService.deleteInstallment(id);
        return ResponseEntity.ok("Installment deleted successfully");
    }
}
