package com.zeta.backend.controller;

import com.zeta.backend.model.BNPLInstallment;
import com.zeta.backend.service.IBNPLPaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class BNPLPaymentControllerTest {

    private IBNPLPaymentService bnplPaymentService;
    private BNPLPaymentController controller;

    @BeforeEach
    public void setup() {
        bnplPaymentService = mock(IBNPLPaymentService.class);
        controller = new BNPLPaymentController(bnplPaymentService);
    }

    @Test
    public void testGetAllInstallmentsByTransactionId() {
        Long transactionId = 1L;
        List<BNPLInstallment> mockList = List.of(new BNPLInstallment());
        when(bnplPaymentService.getAllInstallmentsByTransactionId(transactionId)).thenReturn(mockList);

        ResponseEntity<List<BNPLInstallment>> response = controller.getAllInstallmentsByTransactionId(transactionId);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
    }

    @Test
    public void testPayInstallment() {
        Long installmentId = 1L;
        Double amount = 100.0;

        doNothing().when(bnplPaymentService).payInstallment(installmentId, amount);
        ResponseEntity<String> response = controller.payInstallment(installmentId, amount);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Installment paid successfully", response.getBody());
    }

    @Test
    public void testGetPendingInstallments() {
        Long transactionId = 1L;
        List<BNPLInstallment> mockList = Arrays.asList(new BNPLInstallment());
        when(bnplPaymentService.getPendingInstallmentsByTransactionId(transactionId)).thenReturn(mockList);

        ResponseEntity<List<BNPLInstallment>> response = controller.getPendingInstallments(transactionId);
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    public void testGetOverdueInstallments() {
        Long cardId = 1L;
        when(bnplPaymentService.getOverdueInstallmentsByCardId(cardId)).thenReturn(List.of(new BNPLInstallment()));

        ResponseEntity<List<BNPLInstallment>> response = controller.getOverdueInstallments(cardId);
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    public void testCreateInstallment() {
        BNPLInstallment installment = BNPLInstallment.builder()
                .installmentNumber(1)
                .amount(300.0)
                .dueDate(LocalDate.now().plusDays(30))
                .isPaid(false)
                .build();

        when(bnplPaymentService.createInstallment(any())).thenReturn(installment);

        ResponseEntity<BNPLInstallment> response = controller.createInstallment(installment);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().getInstallmentNumber());
    }

    @Test
    public void testUpdateInstallment() {
        Long id = 1L;
        BNPLInstallment updated = BNPLInstallment.builder().installmentNumber(2).amount(500.0).build();

        when(bnplPaymentService.updateInstallment(eq(id), any())).thenReturn(updated);

        ResponseEntity<BNPLInstallment> response = controller.updateInstallment(id, updated);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2, response.getBody().getInstallmentNumber());
    }

    @Test
    public void testDeleteInstallment() {
        Long id = 1L;
        doNothing().when(bnplPaymentService).deleteInstallment(id);

        ResponseEntity<String> response = controller.deleteInstallment(id);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Installment deleted successfully", response.getBody());
    }
}
