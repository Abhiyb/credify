package com.zeta.backend.controller;

import com.zeta.backend.exception.ResourceNotFoundException;
import com.zeta.backend.model.BNPLInstallment;
import com.zeta.backend.repository.BNPLInstallmentRepository;
import com.zeta.backend.service.LateFeeCalculator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/latefees")
@RequiredArgsConstructor
public class LateFeeController {

    private final LateFeeCalculator lateFeeCalculator;
    private final BNPLInstallmentRepository bnplInstallmentRepository;


    @GetMapping("/{cardId}")
    public ResponseEntity<Double> getLateFeeForCard(@PathVariable Long cardId) {
        double fee = lateFeeCalculator.calculateTotalLateFeeByCardId(cardId);
        return ResponseEntity.ok(fee);
    }

    @GetMapping("/installment/{installmentId}")
    public double getLateFeeForInstallment(@PathVariable Long installmentId) {
        BNPLInstallment installment = bnplInstallmentRepository.findById(installmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Installment not found"));

        return lateFeeCalculator.calculateLateFeeForInstallment(installment);
    }
}
