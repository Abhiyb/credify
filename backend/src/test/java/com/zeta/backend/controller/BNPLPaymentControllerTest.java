package com.zeta.backend.controller;

import com.zeta.backend.model.BNPLInstallment;
import com.zeta.backend.service.IBNPLPaymentService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Slf4j
public class BNPLPaymentControllerTest {

    private IBNPLPaymentService bnplPaymentService; // Mocked BNPL payment service
    private BNPLPaymentController controller; // Controller under test

    @BeforeEach
    public void setup() {
        // Initialize mocks and controller before each test
        bnplPaymentService = mock(IBNPLPaymentService.class);
        controller = new BNPLPaymentController(bnplPaymentService);
        log.info("Setup BNPLPaymentControllerTest with mocked service");
    }

    @Test
    public void testGetAllInstallmentsByTransactionId() {
        Long transactionId = 1L;
        List<BNPLInstallment> mockList = List.of(new BNPLInstallment());

        // Mock service to return a list with one installment for given transactionId
        when(bnplPaymentService.getAllInstallmentsByTransactionId(transactionId)).thenReturn(mockList);

        log.info("Testing getAllInstallmentsByTransactionId with transactionId: {}", transactionId);
        ResponseEntity<List<BNPLInstallment>> response = controller.getAllInstallmentsByTransactionId(transactionId);

        // Assert HTTP 200 OK and correct response size
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
        log.info("getAllInstallmentsByTransactionId returned {} installments", response.getBody().size());
    }

    @Test
    public void testPayInstallment() {
        Long installmentId = 1L;
        Double amount = 100.0;

        // Mock service to do nothing on payInstallment call (void method)
        doNothing().when(bnplPaymentService).payInstallment(installmentId, amount);

        log.info("Testing payInstallment with installmentId: {}, amount: {}", installmentId, amount);
        ResponseEntity<String> response = controller.payInstallment(installmentId, amount);

        // Assert HTTP 200 OK and success message
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Installment paid successfully", response.getBody());
        log.info("payInstallment completed successfully");
    }

    @Test
    public void testGetPendingInstallments() {
        Long transactionId = 1L;
        List<BNPLInstallment> mockList = Arrays.asList(new BNPLInstallment());

        // Mock service to return list of pending installments for given transactionId
        when(bnplPaymentService.getPendingInstallmentsByTransactionId(transactionId)).thenReturn(mockList);

        log.info("Testing getPendingInstallments with transactionId: {}", transactionId);
        ResponseEntity<List<BNPLInstallment>> response = controller.getPendingInstallments(transactionId);

        // Assert HTTP 200 OK and returned list size
        assertEquals(200, response.getStatusCodeValue());
        log.info("getPendingInstallments returned {} installments", response.getBody().size());
    }

    @Test
    public void testGetOverdueInstallments() {
        Long cardId = 1L;

        // Mock service to return list of overdue installments for given cardId
        when(bnplPaymentService.getOverdueInstallmentsByCardId(cardId)).thenReturn(List.of(new BNPLInstallment()));

        log.info("Testing getOverdueInstallments with cardId: {}", cardId);
        ResponseEntity<List<BNPLInstallment>> response = controller.getOverdueInstallments(cardId);

        // Assert HTTP 200 OK and returned list size
        assertEquals(200, response.getStatusCodeValue());
        log.info("getOverdueInstallments returned {} installments", response.getBody().size());
    }

    @Test
    public void testCreateInstallment() {
        // Prepare a sample installment to create
        BNPLInstallment installment = BNPLInstallment.builder()
                .installmentNumber(1)
                .amount(300.0)
                .dueDate(LocalDate.now().plusDays(30))
                .isPaid(false)
                .build();

        // Mock service to return the same installment when creating
        when(bnplPaymentService.createInstallment(any())).thenReturn(installment);

        log.info("Testing createInstallment with installment number: {}", installment.getInstallmentNumber());
        ResponseEntity<BNPLInstallment> response = controller.createInstallment(installment);

        // Assert HTTP 200 OK and returned installment number matches input
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().getInstallmentNumber());
        log.info("createInstallment succeeded");
    }

    @Test
    public void testUpdateInstallment() {
        Long id = 1L;
        BNPLInstallment updated = BNPLInstallment.builder()
                .installmentNumber(2)
                .amount(500.0)
                .build();

        // Mock service to return updated installment on update call
        when(bnplPaymentService.updateInstallment(eq(id), any())).thenReturn(updated);

        log.info("Testing updateInstallment with id: {}", id);
        ResponseEntity<BNPLInstallment> response = controller.updateInstallment(id, updated);

        // Assert HTTP 200 OK and updated installment's number
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2, response.getBody().getInstallmentNumber());
        log.info("updateInstallment succeeded");
    }

    @Test
    public void testDeleteInstallment() {
        Long id = 1L;

        // Mock service to do nothing on deleteInstallment call
        doNothing().when(bnplPaymentService).deleteInstallment(id);

        log.info("Testing deleteInstallment with id: {}", id);
        ResponseEntity<String> response = controller.deleteInstallment(id);

        // Assert HTTP 200 OK and success message
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Installment deleted successfully", response.getBody());
        log.info("deleteInstallment succeeded");
    }
}
