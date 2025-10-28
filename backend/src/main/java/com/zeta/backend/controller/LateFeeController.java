package com.zeta.backend.controller;

import com.zeta.backend.exception.ResourceNotFoundException;
import com.zeta.backend.model.BNPLInstallment;
import com.zeta.backend.repository.BNPLInstallmentRepository;
import com.zeta.backend.service.LateFeeCalculatorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/latefees")
@RequiredArgsConstructor
@Slf4j
public class LateFeeController {

    private final LateFeeCalculatorService lateFeeCalculatorService;
    private final BNPLInstallmentRepository bnplInstallmentRepository;

    /**
     * Endpoint to get total late fee for a specific card
     */
    @GetMapping("/{cardId}")
    public ResponseEntity<Double> getLateFeeForCard(@PathVariable Long cardId) {
        log.info("Calculating total late fee for card ID {}", cardId);
        double fee = lateFeeCalculatorService.calculateTotalLateFeeByCardId(cardId);
        return ResponseEntity.ok(fee);
    }

    /**
     * Endpoint to get late fee for a specific installment
     */
    @GetMapping("/installment/{installmentId}")
    public double getLateFeeForInstallment(@PathVariable Long installmentId) {
        log.info("Calculating late fee for installment ID {}", installmentId);
        BNPLInstallment installment = bnplInstallmentRepository.findById(installmentId)
                .orElseThrow(() -> {
                    log.error("Installment with ID {} not found", installmentId);
                    return new ResourceNotFoundException("Installment not found");
                });

        return lateFeeCalculatorService.calculateLateFeeForInstallment(installment);
    }
}
