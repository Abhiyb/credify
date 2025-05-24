package com.zeta.backend.service;

import com.zeta.backend.model.BNPLInstallment;
import com.zeta.backend.repository.BNPLInstallmentRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Slf4j
class LateFeeCalculatorServiceTest {

    @InjectMocks
    private LateFeeCalculatorService service;

    @Mock
    private BNPLInstallmentRepository repository;

    // Initialize mocks before each test method
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        log.info("Mocks initialized for LateFeeCalculatorServiceTest");
    }

    /**
     * Test late fee calculation for an unpaid installment overdue by 3 days.
     * Late fee is calculated as: (1 + amount * 0.005) * days overdue,
     * rounded to 2 decimals.
     */
    @Test
    void testCalculateLateFeeForInstallment_overdueUnpaid() {
        BNPLInstallment inst = new BNPLInstallment();
        inst.setIsPaid(false);
        inst.setAmount(1000.0);
        inst.setDueDate(LocalDate.now().minusDays(3)); // 3 days overdue

        double expected = (1.0 + (1000.0 * 0.005)) * 3;
        expected = Math.round(expected * 100.0) / 100.0;

        log.info("Calculating late fee for overdue unpaid installment");
        double actual = service.calculateLateFeeForInstallment(inst);
        log.info("Expected late fee: {}, Actual late fee: {}", expected, actual);

        assertEquals(expected, actual);
    }

    /**
     * Test late fee calculation for an installment already paid.
     * Expected late fee is zero.
     */
    @Test
    void testCalculateLateFeeForInstallment_paid() {
        BNPLInstallment inst = new BNPLInstallment();
        inst.setIsPaid(true);
        inst.setAmount(1000.0);
        inst.setDueDate(LocalDate.now().minusDays(5));

        log.info("Calculating late fee for paid installment (should be 0)");
        double actual = service.calculateLateFeeForInstallment(inst);
        log.info("Late fee calculated: {}", actual);

        assertEquals(0.0, actual);
    }

    /**
     * Test late fee calculation for an unpaid installment whose due date is in the future.
     * Expected late fee is zero because it is not overdue.
     */
    @Test
    void testCalculateLateFeeForInstallment_dueInFuture() {
        BNPLInstallment inst = new BNPLInstallment();
        inst.setIsPaid(false);
        inst.setAmount(500.0);
        inst.setDueDate(LocalDate.now().plusDays(2)); // not overdue

        log.info("Calculating late fee for installment due in the future (should be 0)");
        double actual = service.calculateLateFeeForInstallment(inst);
        log.info("Late fee calculated: {}", actual);

        assertEquals(0.0, actual);
    }

    /**
     * Test late fee calculation for an installment with a null due date.
     * Expected late fee is zero as due date is unknown.
     */
    @Test
    void testCalculateLateFeeForInstallment_nullDueDate() {
        BNPLInstallment inst = new BNPLInstallment();
        inst.setIsPaid(false);
        inst.setAmount(300.0);
        inst.setDueDate(null);

        log.info("Calculating late fee for installment with null due date (should be 0)");
        double actual = service.calculateLateFeeForInstallment(inst);
        log.info("Late fee calculated: {}", actual);

        assertEquals(0.0, actual);
    }

    /**
     * Test total late fee calculation for a card by summing late fees of
     * multiple overdue installments.
     * Uses mocked repository to return sample overdue installments.
     */
    @Test
    void testCalculateTotalLateFeeByCardId() {
        BNPLInstallment inst1 = new BNPLInstallment();
        inst1.setIsPaid(false);
        inst1.setAmount(1000.0);
        inst1.setDueDate(LocalDate.now().minusDays(2)); // 2 days overdue

        BNPLInstallment inst2 = new BNPLInstallment();
        inst2.setIsPaid(false);
        inst2.setAmount(2000.0);
        inst2.setDueDate(LocalDate.now().minusDays(1)); // 1 day overdue

        // Mock repository to return two overdue installments for the card ID
        when(repository.findOverdueByCardId(anyLong(), any())).thenReturn(List.of(inst1, inst2));

        double fee1 = (1.0 + (1000.0 * 0.005)) * 2;
        double fee2 = (1.0 + (2000.0 * 0.005)) * 1;

        double expected = Math.round((fee1 + fee2) * 100.0) / 100.0;

        log.info("Calculating total late fee by card id");
        double actual = service.calculateTotalLateFeeByCardId(99L);
        log.info("Expected total late fee: {}, Actual total late fee: {}", expected, actual);

        assertEquals(expected, actual);
    }
}
