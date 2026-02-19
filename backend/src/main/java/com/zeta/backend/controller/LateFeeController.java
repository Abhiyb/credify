package com.zeta.backend.controller;

import com.zeta.backend.exception.ResourceNotFoundException;
import com.zeta.backend.model.BNPLInstallment;
import com.zeta.backend.repository.BNPLInstallmentRepository;
import com.zeta.backend.service.implementation.LateFeeCalculatorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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
     * JWT-secured: user can only access their own cards
     */
    @GetMapping("/{cardId}")
    public ResponseEntity<Double> getLateFeeForCard(
            @PathVariable Long cardId,
            @AuthenticationPrincipal UserDetails userDetails) {

        String currentUserEmail = userDetails.getUsername();
        log.info("User {} requested total late fee for card ID {}", currentUserEmail, cardId);

        // Check card ownership
        String cardOwnerEmail = bnplInstallmentRepository.findCardOwnerEmailByCardId(cardId)
                .orElseThrow(() -> new ResourceNotFoundException("Card not found"));

        if (!cardOwnerEmail.equals(currentUserEmail)) {
            return ResponseEntity.status(403).build(); // Forbidden
        }

        double totalFee = lateFeeCalculatorService.calculateTotalLateFeeByCardId(cardId);
        return ResponseEntity.ok(totalFee);
    }

    /**
     * Endpoint to get late fee for a specific installment
     * JWT-secured: user can only access their own installments
     */
    @GetMapping("/installment/{installmentId}")
    public ResponseEntity<Double> getLateFeeForInstallment(
            @PathVariable Long installmentId,
            @AuthenticationPrincipal UserDetails userDetails) {

        String currentUserEmail = userDetails.getUsername();
        log.info("User {} requested late fee for installment ID {}", currentUserEmail, installmentId);

        BNPLInstallment installment = bnplInstallmentRepository.findById(installmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Installment not found"));

        // Check ownership
        String ownerEmail = installment.getTransaction().getCard().getUser().getEmail();
        if (!ownerEmail.equals(currentUserEmail)) {
            return ResponseEntity.status(403).build();
        }

        double fee = lateFeeCalculatorService.calculateLateFeeForInstallment(installment);
        return ResponseEntity.ok(fee);
    }
}
