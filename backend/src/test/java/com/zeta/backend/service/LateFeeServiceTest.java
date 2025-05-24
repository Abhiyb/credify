package com.zeta.backend.service;

import com.zeta.backend.model.BNPLInstallment;
import com.zeta.backend.repository.BNPLInstallmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LateFeeCalculatorServiceTest {

    @InjectMocks
    private LateFeeCalculatorService service;

    @Mock
    private BNPLInstallmentRepository repository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCalculateLateFeeForInstallment_overdueUnpaid() {
        BNPLInstallment inst = new BNPLInstallment();
        inst.setIsPaid(false);
        inst.setAmount(1000.0);
        inst.setDueDate(LocalDate.now().minusDays(3)); // 3 days late

        double expected = (1.0 + (1000.0 * 0.005)) * 3; // 3 * (1 + 5)
        expected = Math.round(expected * 100.0) / 100.0;

        double actual = service.calculateLateFeeForInstallment(inst);
        assertEquals(expected, actual);
    }

    @Test
    void testCalculateLateFeeForInstallment_paid() {
        BNPLInstallment inst = new BNPLInstallment();
        inst.setIsPaid(true);
        inst.setAmount(1000.0);
        inst.setDueDate(LocalDate.now().minusDays(5));

        assertEquals(0.0, service.calculateLateFeeForInstallment(inst));
    }

    @Test
    void testCalculateLateFeeForInstallment_dueInFuture() {
        BNPLInstallment inst = new BNPLInstallment();
        inst.setIsPaid(false);
        inst.setAmount(500.0);
        inst.setDueDate(LocalDate.now().plusDays(2)); // not overdue

        assertEquals(0.0, service.calculateLateFeeForInstallment(inst));
    }

    @Test
    void testCalculateLateFeeForInstallment_nullDueDate() {
        BNPLInstallment inst = new BNPLInstallment();
        inst.setIsPaid(false);
        inst.setAmount(300.0);
        inst.setDueDate(null);

        assertEquals(0.0, service.calculateLateFeeForInstallment(inst));
    }

    @Test
    void testCalculateTotalLateFeeByCardId() {
        BNPLInstallment inst1 = new BNPLInstallment();
        inst1.setIsPaid(false);
        inst1.setAmount(1000.0);
        inst1.setDueDate(LocalDate.now().minusDays(2)); // 2 days late

        BNPLInstallment inst2 = new BNPLInstallment();
        inst2.setIsPaid(false);
        inst2.setAmount(2000.0);
        inst2.setDueDate(LocalDate.now().minusDays(1)); // 1 day late

        when(repository.findOverdueByCardId(anyLong(), any())).thenReturn(List.of(inst1, inst2));

        double fee1 = (1.0 + (1000.0 * 0.005)) * 2;
        double fee2 = (1.0 + (2000.0 * 0.005)) * 1;

        double expected = Math.round((fee1 + fee2) * 100.0) / 100.0;
        double actual = service.calculateTotalLateFeeByCardId(99L);

        assertEquals(expected, actual);
    }
}
