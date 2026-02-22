package com.zeta.backend.controller;

import com.zeta.backend.dto.BNPLInstallmentCreateDTO;
import com.zeta.backend.dto.BNPLInstallmentResponseDTO;
import com.zeta.backend.dto.BNPLInstallmentUpdateDTO;
import com.zeta.backend.exception.BadRequestException;
import com.zeta.backend.service.IBNPLPaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing BNPL installment-related API endpoints.
 * Handles payment processing, retrieval, creation, updates, and deletion of installments.
 * Uses DTOs for secure data transfer and logging for monitoring.
 */
@RestController
@RequestMapping("/api/bnpl/installments")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "http://localhost:3000")
public class BNPLPaymentController {

    private final IBNPLPaymentService bnplPaymentService;

    /**
     * Processes a payment for an installment.
     * @param id ID of the installment to pay.
     * @param amount Payment amount.
     * @return ResponseEntity with BNPLInstallmentResponseDTO.
     * @throws BadRequestException if amount is invalid.
     */

    @PostMapping("/{id}/pay")
    public ResponseEntity<BNPLInstallmentResponseDTO> payInstallment(
            Authentication authentication,
            @PathVariable Long id,
            @RequestParam Double amount) {

        String email = authentication.getName();
        BNPLInstallmentResponseDTO response = bnplPaymentService.payInstallment(id, amount, email);
        return ResponseEntity.ok(response);
    }


    /**
     * Retrieves pending (unpaid) installments for a transaction.
     * @param transactionId ID of the transaction to fetch pending installments for.
     * @return ResponseEntity with List of BNPLInstallmentResponseDTOs.
     */
    @GetMapping("/transaction/{transactionId}/pending")
    public ResponseEntity<List<BNPLInstallmentResponseDTO>> getPendingInstallments(
            Authentication authentication,
            @PathVariable Long transactionId) {
        String email = authentication.getName(); // get logged-in user
        List<BNPLInstallmentResponseDTO> installments = bnplPaymentService.getPendingInstallmentsByTransactionId(transactionId, email);
        return ResponseEntity.ok(installments);
    }


    /**
     * Retrieves overdue installments for a card.
     * @param cardId ID of the card to fetch overdue installments for.
     * @return ResponseEntity with List of BNPLInstallmentResponseDTOs.
     */
    @GetMapping("/card/{cardId}/overdue")
    public ResponseEntity<List<BNPLInstallmentResponseDTO>> getOverdueInstallments(
            Authentication authentication,
            @PathVariable Long cardId) {
        String email = authentication.getName(); // get logged-in user
        List<BNPLInstallmentResponseDTO> installments = bnplPaymentService.getOverdueInstallmentsByCardId(cardId, email);
        return ResponseEntity.ok(installments);
    }


    /**
     * Retrieves all installments in the system.
     * @return ResponseEntity with List of BNPLInstallmentResponseDTOs.
     */
    @GetMapping
    public ResponseEntity<List<BNPLInstallmentResponseDTO>> getAllInstallments() {
        log.info("Received request to fetch all installments");
        List<BNPLInstallmentResponseDTO> installments = bnplPaymentService.getAllInstallments();
        log.debug("Returning {} installments", installments.size());
        return ResponseEntity.ok(installments);
    }

    /**
     * Retrieves an installment by its ID.
     * @param id ID of the installment to fetch.
     * @return ResponseEntity with BNPLInstallmentResponseDTO.
     */
    @GetMapping("/{id}")
    public ResponseEntity<BNPLInstallmentResponseDTO> getInstallmentById(@PathVariable Long id) {
        log.info("Received request to fetch installment with ID: {}", id);
        BNPLInstallmentResponseDTO installment = bnplPaymentService.getInstallmentById(id);
        log.debug("Returning installment with ID: {}", id);
        return ResponseEntity.ok(installment);
    }

    /**
     * Creates a new BNPL installment.
     * @param installment dto containing installment details.
     * @return ResponseEntity with BNPLInstallmentResponseDTO.
     * @throws BadRequestException if dto validation fails.
     */
    @PostMapping
    public ResponseEntity<BNPLInstallmentResponseDTO> createInstallment(
            @Valid @RequestBody BNPLInstallmentCreateDTO installment) {
        log.info("Received request to create installment for transaction ID: {}", installment.getTransactionId());
        BNPLInstallmentResponseDTO response = bnplPaymentService.createInstallment(installment);
        log.info("Created installment with ID: {}", response.getId());
        return ResponseEntity.ok(response);
    }

    /**
     * Updates an existing BNPL installment.
     * @param id ID of the installment to update.
     * @param installment dto containing updated installment details.
     * @return ResponseEntity with BNPLInstallmentResponseDTO.
     * @throws BadRequestException if dto validation fails.
     */
    @PutMapping("/{id}")
    public ResponseEntity<BNPLInstallmentResponseDTO> updateInstallment(
            @PathVariable Long id,
            @Valid @RequestBody BNPLInstallmentUpdateDTO installment) {
        log.info("Received request to update installment with ID: {}", id);
        BNPLInstallmentResponseDTO response = bnplPaymentService.updateInstallment(id, installment);
        log.info("Updated installment with ID: {}", id);
        return ResponseEntity.ok(response);
    }

    /**
     * Deletes an existing BNPL installment.
     * @param id ID of the installment to delete.
     * @return ResponseEntity with no content.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInstallment(@PathVariable Long id) {
        log.info("Received request to delete installment with ID: {}", id);
        bnplPaymentService.deleteInstallment(id);
        log.info("Deleted installment with ID: {}", id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Retrieves all installments for a transaction.
     * @param transactionId ID of the transaction to fetch installments for.
     * @return ResponseEntity with List of BNPLInstallmentResponseDTOs.
     */
    @GetMapping("/transaction/{transactionId}")
    public ResponseEntity<List<BNPLInstallmentResponseDTO>> getAllInstallmentsByTransactionId(
            @PathVariable Long transactionId) {
        log.info("Received request to fetch all installments for transaction ID: {}", transactionId);
        List<BNPLInstallmentResponseDTO> installments = bnplPaymentService.getAllInstallmentsByTransactionId(transactionId);
        log.debug("Returning {} installments for transaction ID: {}", installments.size(), transactionId);
        return ResponseEntity.ok(installments);
    }
}
