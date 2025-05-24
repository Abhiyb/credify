package com.zeta.backend.controller;

import com.zeta.backend.exception.ResourceNotFoundException;
import com.zeta.backend.model.BNPLInstallment;
import com.zeta.backend.repository.BNPLInstallmentRepository;
import com.zeta.backend.service.LateFeeCalculatorService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Slf4j
public class LateFeeControllerTest {

    private LateFeeCalculatorService lateFeeCalculatorService;
    private BNPLInstallmentRepository bnplInstallmentRepository;
    private LateFeeController controller;

    // Initialize mocks and controller before each test
    @BeforeEach
    public void setUp() {
        lateFeeCalculatorService = mock(LateFeeCalculatorService.class);
        bnplInstallmentRepository = mock(BNPLInstallmentRepository.class);
        controller = new LateFeeController(lateFeeCalculatorService, bnplInstallmentRepository);
        log.info("Setup LateFeeControllerTest with mocked dependencies");
    }

    /**
     * Test fetching total late fee for a card.
     * Verifies that the controller returns the correct fee from the service.
     */
    @Test
    public void testGetLateFeeForCard() {
        Long cardId = 1L;
        // Mock the service to return a fixed late fee
        when(lateFeeCalculatorService.calculateTotalLateFeeByCardId(cardId)).thenReturn(150.0);

        log.info("Testing getLateFeeForCard with cardId: {}", cardId);
        ResponseEntity<Double> response = controller.getLateFeeForCard(cardId);

        // Assert HTTP 200 OK and correct fee value
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(150.0, response.getBody());
        log.info("Late fee for card {} is {}", cardId, response.getBody());
    }

    /**
     * Test fetching late fee for a specific installment.
     * Verifies that the controller calculates the late fee correctly using the repository and service.
     */
    @Test
    public void testGetLateFeeForInstallment() {
        Long installmentId = 1L;
        // Create a mock installment with past due date and unpaid status
        BNPLInstallment installment = BNPLInstallment.builder()
                .id(installmentId)
                .installmentNumber(1)
                .amount(100.0)
                .dueDate(LocalDate.now().minusDays(10))
                .isPaid(false)
                .build();

        // Mock repository to return the installment
        when(bnplInstallmentRepository.findById(installmentId)).thenReturn(Optional.of(installment));
        // Mock service to calculate late fee for the installment
        when(lateFeeCalculatorService.calculateLateFeeForInstallment(installment)).thenReturn(20.0);

        log.info("Testing getLateFeeForInstallment with installmentId: {}", installmentId);
        double fee = controller.getLateFeeForInstallment(installmentId);

        // Assert that returned fee matches expected value
        assertEquals(20.0, fee);
        log.info("Late fee for installment {} is {}", installmentId, fee);
    }

    /**
     * Test behavior when installment ID is not found.
     * Verifies that the controller throws ResourceNotFoundException appropriately.
     */
    @Test
    public void testGetLateFeeForInstallment_NotFound() {
        Long installmentId = 99L;
        // Mock repository to return empty for unknown ID
        when(bnplInstallmentRepository.findById(installmentId)).thenReturn(Optional.empty());

        log.info("Testing getLateFeeForInstallment with invalid installmentId: {}", installmentId);

        // Expect ResourceNotFoundException when calling with invalid ID
        assertThrows(ResourceNotFoundException.class, () -> {
            controller.getLateFeeForInstallment(installmentId);
        });
        log.info("ResourceNotFoundException thrown as expected for installmentId {}", installmentId);
    }
}
