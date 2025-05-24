package com.zeta.backend.service;

import com.zeta.backend.exception.ResourceNotFoundException;
import com.zeta.backend.model.BNPLInstallment;
import com.zeta.backend.repository.BNPLInstallmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BNPLPaymentServiceTest {

    @InjectMocks
    private BNPLPaymentService service;

    @Mock
    private BNPLInstallmentRepository repository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testPayInstallment_success() {
        BNPLInstallment inst = new BNPLInstallment();
        inst.setAmount(500.0);
        inst.setIsPaid(false);
        when(repository.findById(1L)).thenReturn(Optional.of(inst));

        service.payInstallment(1L, 500.0);

        assertTrue(inst.getIsPaid());
        verify(repository).save(inst);
    }

    @Test
    void testPayInstallment_amountMismatch() {
        BNPLInstallment inst = new BNPLInstallment();
        inst.setAmount(500.0);
        when(repository.findById(1L)).thenReturn(Optional.of(inst));

        assertThrows(IllegalArgumentException.class, () -> service.payInstallment(1L, 600.0));
    }

    @Test
    void testPayInstallment_notFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.payInstallment(1L, 500.0));
    }

    @Test
    void testGetPendingInstallmentsByTransactionId() {
        List<BNPLInstallment> list = List.of(new BNPLInstallment());
        when(repository.findByTransaction_IdAndIsPaidFalse(10L)).thenReturn(list);

        List<BNPLInstallment> result = service.getPendingInstallmentsByTransactionId(10L);
        assertEquals(1, result.size());
    }

    @Test
    void testGetOverdueInstallmentsByCardId() {
        List<BNPLInstallment> list = List.of(new BNPLInstallment());
        when(repository.findByTransaction_Card_CardIdAndIsPaidFalseAndDueDateBefore(anyLong(), any()))
                .thenReturn(list);

        List<BNPLInstallment> result = service.getOverdueInstallmentsByCardId(5L);
        assertEquals(1, result.size());
    }

    @Test
    void testGetAllInstallmentsByTransactionId() {
        List<BNPLInstallment> list = List.of(new BNPLInstallment());
        when(repository.findByTransactionId(20L)).thenReturn(list);

        List<BNPLInstallment> result = service.getAllInstallmentsByTransactionId(20L);
        assertEquals(1, result.size());
    }

    @Test
    void testCRUDMethods() {
        BNPLInstallment inst = new BNPLInstallment();
        inst.setInstallmentNumber(1);

        when(repository.findAll()).thenReturn(List.of(inst));
        when(repository.findById(1L)).thenReturn(Optional.of(inst));
        when(repository.save(any())).thenReturn(inst);

        assertEquals(1, service.getAllInstallments().size());
        assertEquals(inst, service.getInstallmentById(1L));
        assertEquals(inst, service.createInstallment(inst));
        assertEquals(inst, service.updateInstallment(1L, inst));

        service.deleteInstallment(1L);
        verify(repository).delete(inst);
    }

    @Test
    void testGetInstallmentById_notFound() {
        when(repository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.getInstallmentById(2L));
    }
}
