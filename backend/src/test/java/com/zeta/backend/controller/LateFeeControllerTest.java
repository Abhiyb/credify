package com.zeta.backend.controller;

import com.zeta.backend.exception.ResourceNotFoundException;
import com.zeta.backend.model.BNPLInstallment;
import com.zeta.backend.repository.BNPLInstallmentRepository;
import com.zeta.backend.service.LateFeeCalculatorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class LateFeeControllerTest {

    private LateFeeCalculatorService lateFeeCalculatorService;
    private BNPLInstallmentRepository bnplInstallmentRepository;
    private LateFeeController controller;

    @BeforeEach
    public void setUp() {
        lateFeeCalculatorService = mock(LateFeeCalculatorService.class);
        bnplInstallmentRepository = mock(BNPLInstallmentRepository.class);
        controller = new LateFeeController(lateFeeCalculatorService, bnplInstallmentRepository);
    }

    @Test
    public void testGetLateFeeForCard() {
        Long cardId = 1L;
        when(lateFeeCalculatorService.calculateTotalLateFeeByCardId(cardId)).thenReturn(150.0);

        ResponseEntity<Double> response = controller.getLateFeeForCard(cardId);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(150.0, response.getBody());
    }

    @Test
    public void testGetLateFeeForInstallment() {
        Long installmentId = 1L;
        BNPLInstallment installment = BNPLInstallment.builder()
                .id(installmentId)
                .installmentNumber(1)
                .amount(100.0)
                .dueDate(LocalDate.now().minusDays(10))
                .isPaid(false)
                .build();

        when(bnplInstallmentRepository.findById(installmentId)).thenReturn(Optional.of(installment));
        when(lateFeeCalculatorService.calculateLateFeeForInstallment(installment)).thenReturn(20.0);

        double fee = controller.getLateFeeForInstallment(installmentId);
        assertEquals(20.0, fee);
    }

    @Test
    public void testGetLateFeeForInstallment_NotFound() {
        Long installmentId = 99L;
        when(bnplInstallmentRepository.findById(installmentId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            controller.getLateFeeForInstallment(installmentId);
        });
    }
}
