package com.zeta.backend.controller;

import com.zeta.backend.dto.BNPLInstallmentCreateDTO;
import com.zeta.backend.dto.BNPLInstallmentResponseDTO;
import com.zeta.backend.dto.BNPLInstallmentUpdateDTO;
import com.zeta.backend.exception.BadRequestException;
import com.zeta.backend.exception.ResourceNotFoundException;
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
class BNPLPaymentControllerTest {

    private IBNPLPaymentService bnplPaymentService;
    private BNPLPaymentController controller;

    @BeforeEach
    void setup() {
        bnplPaymentService = mock(IBNPLPaymentService.class);
        controller = new BNPLPaymentController(bnplPaymentService);
        log.info("Setup BNPLPaymentControllerTest with mocked service");
    }

    @Test
    void testPayInstallment_Success() {
        Long installmentId = 1L;
        Double amount = 100.0;
        BNPLInstallmentResponseDTO responseDTO = new BNPLInstallmentResponseDTO();
        responseDTO.setId(installmentId);
        responseDTO.setAmount(amount);
        responseDTO.setIsPaid(true);

        when(bnplPaymentService.payInstallment(installmentId, amount)).thenReturn(responseDTO);

        log.info("Testing payInstallment with installmentId: {}, amount: {}", installmentId, amount);
        ResponseEntity<BNPLInstallmentResponseDTO> response = controller.payInstallment(installmentId, amount);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(responseDTO, response.getBody());
        verify(bnplPaymentService, times(1)).payInstallment(installmentId, amount);
        log.info("payInstallment completed successfully");
    }

    @Test
    void testPayInstallment_NotFound() {
        Long installmentId = 1L;
        Double amount = 100.0;

        when(bnplPaymentService.payInstallment(installmentId, amount))
                .thenThrow(new ResourceNotFoundException("Installment not found"));

        log.info("Testing payInstallment with nonexistent installmentId: {}", installmentId);
        assertThrows(ResourceNotFoundException.class, () -> controller.payInstallment(installmentId, amount));
        verify(bnplPaymentService, times(1)).payInstallment(installmentId, amount);
        log.info("ResourceNotFoundException thrown as expected");
    }

    @Test
    void testPayInstallment_InvalidAmount() {
        Long installmentId = 1L;
        Double amount = 100.0;

        when(bnplPaymentService.payInstallment(installmentId, amount))
                .thenThrow(new BadRequestException("Payment amount must match installment amount"));

        log.info("Testing payInstallment with invalid amount for installmentId: {}", installmentId);
        assertThrows(BadRequestException.class, () -> controller.payInstallment(installmentId, amount));
        verify(bnplPaymentService, times(1)).payInstallment(installmentId, amount);
        log.info("BadRequestException thrown as expected");
    }

    @Test
    void testGetPendingInstallments() {
        Long transactionId = 1L;
        BNPLInstallmentResponseDTO responseDTO = new BNPLInstallmentResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setTransactionId(transactionId);
        List<BNPLInstallmentResponseDTO> mockList = Arrays.asList(responseDTO);

        when(bnplPaymentService.getPendingInstallmentsByTransactionId(transactionId)).thenReturn(mockList);

        log.info("Testing getPendingInstallments with transactionId: {}", transactionId);
        ResponseEntity<List<BNPLInstallmentResponseDTO>> response = controller.getPendingInstallments(transactionId);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
        verify(bnplPaymentService, times(1)).getPendingInstallmentsByTransactionId(transactionId);
        log.info("getPendingInstallments returned {} installments", response.getBody().size());
    }

    @Test
    void testGetOverdueInstallments() {
        Long cardId = 1L;
        BNPLInstallmentResponseDTO responseDTO = new BNPLInstallmentResponseDTO();
        responseDTO.setId(1L);
        List<BNPLInstallmentResponseDTO> mockList = Arrays.asList(responseDTO);

        when(bnplPaymentService.getOverdueInstallmentsByCardId(cardId)).thenReturn(mockList);

        log.info("Testing getOverdueInstallments with cardId: {}", cardId);
        ResponseEntity<List<BNPLInstallmentResponseDTO>> response = controller.getOverdueInstallments(cardId);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
        verify(bnplPaymentService, times(1)).getOverdueInstallmentsByCardId(cardId);
        log.info("getOverdueInstallments returned {} installments", response.getBody().size());
    }

    @Test
    void testGetAllInstallments() {
        BNPLInstallmentResponseDTO responseDTO = new BNPLInstallmentResponseDTO();
        responseDTO.setId(1L);
        List<BNPLInstallmentResponseDTO> mockList = Arrays.asList(responseDTO);

        when(bnplPaymentService.getAllInstallments()).thenReturn(mockList);

        log.info("Testing getAllInstallments");
        ResponseEntity<List<BNPLInstallmentResponseDTO>> response = controller.getAllInstallments();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
        verify(bnplPaymentService, times(1)).getAllInstallments();
        log.info("getAllInstallments returned {} installments", response.getBody().size());
    }

    @Test
    void testGetInstallmentById_Success() {
        Long id = 1L;
        BNPLInstallmentResponseDTO responseDTO = new BNPLInstallmentResponseDTO();
        responseDTO.setId(id);

        when(bnplPaymentService.getInstallmentById(id)).thenReturn(responseDTO);

        log.info("Testing getInstallmentById with id: {}", id);
        ResponseEntity<BNPLInstallmentResponseDTO> response = controller.getInstallmentById(id);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(responseDTO, response.getBody());
        verify(bnplPaymentService, times(1)).getInstallmentById(id);
        log.info("getInstallmentById succeeded");
    }

    @Test
    void testGetInstallmentById_NotFound() {
        Long id = 1L;

        when(bnplPaymentService.getInstallmentById(id))
                .thenThrow(new ResourceNotFoundException("Installment not found"));

        log.info("Testing getInstallmentById with nonexistent id: {}", id);
        assertThrows(ResourceNotFoundException.class, () -> controller.getInstallmentById(id));
        verify(bnplPaymentService, times(1)).getInstallmentById(id);
        log.info("ResourceNotFoundException thrown as expected");
    }

    @Test
    void testCreateInstallment_Success() {
        BNPLInstallmentCreateDTO createDTO = new BNPLInstallmentCreateDTO();
        createDTO.setTransactionId(1L);
        createDTO.setInstallmentNumber(1);
        createDTO.setAmount(300.0);
        createDTO.setDueDate(LocalDate.now().plusDays(30));
        createDTO.setIsPaid(false);

        BNPLInstallmentResponseDTO responseDTO = new BNPLInstallmentResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setTransactionId(1L);
        responseDTO.setInstallmentNumber(1);
        responseDTO.setAmount(300.0);

        when(bnplPaymentService.createInstallment(createDTO)).thenReturn(responseDTO);

        log.info("Testing createInstallment with installment number: {}", createDTO.getInstallmentNumber());
        ResponseEntity<BNPLInstallmentResponseDTO> response = controller.createInstallment(createDTO);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().getInstallmentNumber());
        verify(bnplPaymentService, times(1)).createInstallment(createDTO);
        log.info("createInstallment succeeded");
    }

    @Test
    void testCreateInstallment_TransactionNotFound() {
        BNPLInstallmentCreateDTO createDTO = new BNPLInstallmentCreateDTO();
        createDTO.setTransactionId(1L);
        createDTO.setInstallmentNumber(1);
        createDTO.setAmount(300.0);

        when(bnplPaymentService.createInstallment(createDTO))
                .thenThrow(new ResourceNotFoundException("Transaction not found"));

        log.info("Testing createInstallment with nonexistent transaction");
        assertThrows(ResourceNotFoundException.class, () -> controller.createInstallment(createDTO));
        verify(bnplPaymentService, times(1)).createInstallment(createDTO);
        log.info("ResourceNotFoundException thrown as expected");
    }

    @Test
    void testUpdateInstallment_Success() {
        Long id = 1L;
        BNPLInstallmentUpdateDTO updateDTO = new BNPLInstallmentUpdateDTO();
        updateDTO.setInstallmentNumber(2);
        updateDTO.setAmount(500.0);

        BNPLInstallmentResponseDTO responseDTO = new BNPLInstallmentResponseDTO();
        responseDTO.setId(id);
        responseDTO.setInstallmentNumber(2);
        responseDTO.setAmount(500.0);

        when(bnplPaymentService.updateInstallment(id, updateDTO)).thenReturn(responseDTO);

        log.info("Testing updateInstallment with id: {}", id);
        ResponseEntity<BNPLInstallmentResponseDTO> response = controller.updateInstallment(id, updateDTO);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2, response.getBody().getInstallmentNumber());
        verify(bnplPaymentService, times(1)).updateInstallment(id, updateDTO);
        log.info("updateInstallment succeeded");
    }

    @Test
    void testUpdateInstallment_NotFound() {
        Long id = 1L;
        BNPLInstallmentUpdateDTO updateDTO = new BNPLInstallmentUpdateDTO();
        updateDTO.setAmount(500.0);

        when(bnplPaymentService.updateInstallment(id, updateDTO))
                .thenThrow(new ResourceNotFoundException("Installment not found"));

        log.info("Testing updateInstallment with nonexistent id: {}", id);
        assertThrows(ResourceNotFoundException.class, () -> controller.updateInstallment(id, updateDTO));
        verify(bnplPaymentService, times(1)).updateInstallment(id, updateDTO);
        log.info("ResourceNotFoundException thrown as expected");
    }

    @Test
    void testDeleteInstallment_Success() {
        Long id = 1L;

        doNothing().when(bnplPaymentService).deleteInstallment(id);

        log.info("Testing deleteInstallment with id: {}", id);
        ResponseEntity<Void> response = controller.deleteInstallment(id);

        assertEquals(204, response.getStatusCodeValue());
        verify(bnplPaymentService, times(1)).deleteInstallment(id);
        log.info("deleteInstallment succeeded");
    }

    @Test
    void testDeleteInstallment_NotFound() {
        Long id = 1L;

        doThrow(new ResourceNotFoundException("Installment not found")).when(bnplPaymentService).deleteInstallment(id);

        log.info("Testing deleteInstallment with nonexistent id: {}", id);
        assertThrows(ResourceNotFoundException.class, () -> controller.deleteInstallment(id));
        verify(bnplPaymentService, times(1)).deleteInstallment(id);
        log.info("ResourceNotFoundException thrown as expected");
    }

    @Test
    void testGetAllInstallmentsByTransactionId() {
        Long transactionId = 1L;
        BNPLInstallmentResponseDTO responseDTO = new BNPLInstallmentResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setTransactionId(transactionId);
        List<BNPLInstallmentResponseDTO> mockList = Arrays.asList(responseDTO);

        when(bnplPaymentService.getAllInstallmentsByTransactionId(transactionId)).thenReturn(mockList);

        log.info("Testing getAllInstallmentsByTransactionId with transactionId: {}", transactionId);
        ResponseEntity<List<BNPLInstallmentResponseDTO>> response = controller.getAllInstallmentsByTransactionId(transactionId);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
        verify(bnplPaymentService, times(1)).getAllInstallmentsByTransactionId(transactionId);
        log.info("getAllInstallmentsByTransactionId returned {} installments", response.getBody().size());
    }
}