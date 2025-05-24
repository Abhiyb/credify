package com.zeta.backend.service;

import com.zeta.backend.exception.ResourceNotFoundException;
import com.zeta.backend.model.BNPLInstallment;
import com.zeta.backend.repository.BNPLInstallmentRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
class BNPLPaymentServiceTest {

    @InjectMocks
    private BNPLPaymentService service;  // Service under test

    @Mock
    private BNPLInstallmentRepository repository;  // Mock repository dependency

    // Test successful payment of an installment when the paid amount matches the installment amount
    @Test
    void testPayInstallment_success() {
        BNPLInstallment inst = new BNPLInstallment();
        inst.setAmount(500.0);
        inst.setIsPaid(false);
        // Mock repository to return this installment for id 1L
        when(repository.findById(1L)).thenReturn(Optional.of(inst));

        log.info("Testing payInstallment with correct amount");
        service.payInstallment(1L, 500.0);

        // After payment, installment should be marked as paid
        assertTrue(inst.getIsPaid());
        log.info("Installment marked as paid successfully");

        // Verify that the repository save method was called to persist changes
        verify(repository).save(inst);
    }

    // Test payment with incorrect amount should throw IllegalArgumentException
    @Test
    void testPayInstallment_amountMismatch() {
        BNPLInstallment inst = new BNPLInstallment();
        inst.setAmount(500.0);
        when(repository.findById(1L)).thenReturn(Optional.of(inst));

        log.info("Testing payInstallment with incorrect amount");
        // Expect exception due to amount mismatch
        assertThrows(IllegalArgumentException.class, () -> service.payInstallment(1L, 600.0));
        log.info("IllegalArgumentException thrown as expected");
    }

    // Test behavior when the installment id does not exist - should throw ResourceNotFoundException
    @Test
    void testPayInstallment_notFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());
        log.info("Testing payInstallment with nonexistent installment");
        assertThrows(ResourceNotFoundException.class, () -> service.payInstallment(1L, 500.0));
        log.info("ResourceNotFoundException thrown as expected");
    }

    // Test retrieval of pending (unpaid) installments by transaction id
    @Test
    void testGetPendingInstallmentsByTransactionId() {
        List<BNPLInstallment> list = List.of(new BNPLInstallment());
        when(repository.findByTransaction_IdAndIsPaidFalse(10L)).thenReturn(list);

        log.info("Testing getPendingInstallmentsByTransactionId");
        List<BNPLInstallment> result = service.getPendingInstallmentsByTransactionId(10L);

        // Should return exactly one pending installment
        assertEquals(1, result.size());
        log.info("Pending installments returned: {}", result.size());
    }

    // Test retrieval of overdue unpaid installments by card id
    @Test
    void testGetOverdueInstallmentsByCardId() {
        List<BNPLInstallment> list = List.of(new BNPLInstallment());
        when(repository.findByTransaction_Card_CardIdAndIsPaidFalseAndDueDateBefore(anyLong(), any()))
                .thenReturn(list);

        log.info("Testing getOverdueInstallmentsByCardId");
        List<BNPLInstallment> result = service.getOverdueInstallmentsByCardId(5L);

        // Should return exactly one overdue installment
        assertEquals(1, result.size());
        log.info("Overdue installments returned: {}", result.size());
    }

    // Test retrieval of all installments (paid and unpaid) by transaction id
    @Test
    void testGetAllInstallmentsByTransactionId() {
        List<BNPLInstallment> list = List.of(new BNPLInstallment());
        when(repository.findByTransactionId(20L)).thenReturn(list);

        log.info("Testing getAllInstallmentsByTransactionId");
        List<BNPLInstallment> result = service.getAllInstallmentsByTransactionId(20L);

        // Should return exactly one installment
        assertEquals(1, result.size());
        log.info("All installments returned: {}", result.size());
    }

    // Test basic CRUD operations using the repository mock
    @Test
    void testCRUDMethods() {
        BNPLInstallment inst = new BNPLInstallment();
        inst.setInstallmentNumber(1);

        // Mock repository CRUD responses
        when(repository.findAll()).thenReturn(List.of(inst));
        when(repository.findById(1L)).thenReturn(Optional.of(inst));
        when(repository.save(any())).thenReturn(inst);

        log.info("Testing CRUD methods");

        // Test getting all installments returns the list with one item
        assertEquals(1, service.getAllInstallments().size());
        log.info("getAllInstallments returned 1");

        // Test getInstallmentById returns the correct installment
        assertEquals(inst, service.getInstallmentById(1L));
        log.info("getInstallmentById returned installment with number {}", inst.getInstallmentNumber());

        // Test creating installment returns the created installment
        assertEquals(inst, service.createInstallment(inst));
        log.info("createInstallment returned created installment");

        // Test updating installment returns the updated installment
        assertEquals(inst, service.updateInstallment(1L, inst));
        log.info("updateInstallment updated installment");

        // Test deleting installment calls repository.delete
        service.deleteInstallment(1L);
        verify(repository).delete(inst);
        log.info("deleteInstallment deleted installment");
    }

    // Test getInstallmentById when the installment is not found, should throw ResourceNotFoundException
    @Test
    void testGetInstallmentById_notFound() {
        when(repository.findById(2L)).thenReturn(Optional.empty());
        log.info("Testing getInstallmentById with nonexistent id");
        assertThrows(ResourceNotFoundException.class, () -> service.getInstallmentById(2L));
        log.info("ResourceNotFoundException thrown as expected");
    }
}
