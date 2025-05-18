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
}
