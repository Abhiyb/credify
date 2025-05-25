package com.zeta.backend.service;

import com.zeta.backend.dto.BNPLInstallmentCreateDTO;
import com.zeta.backend.dto.BNPLInstallmentResponseDTO;
import com.zeta.backend.dto.BNPLInstallmentUpdateDTO;
import com.zeta.backend.exception.BadRequestException;
import com.zeta.backend.model.BNPLInstallment;
import com.zeta.backend.model.Transaction;
import com.zeta.backend.repository.BNPLInstallmentRepository;
import com.zeta.backend.repository.TransactionRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for BNPLPaymentService.
 * Tests various service methods including payment processing, retrieval, creation, update, and deletion.
 */
@Slf4j
class BNPLPaymentServiceTest {

    @Mock
    private BNPLInstallmentRepository installmentRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private LateFeeCalculatorService lateFeeCalculatorService;

    @InjectMocks
    private BNPLPaymentService bnplPaymentService;

    private BNPLInstallment installment;
    private Transaction transaction;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        transaction = new Transaction();
        transaction.setId(1L);

        installment = new BNPLInstallment();
        installment.setId(100L);
        installment.setTransaction(transaction);
        installment.setInstallmentNumber(1);
        installment.setAmount(500.0);
        installment.setDueDate(LocalDate.now().plusDays(10));
        installment.setIsPaid(false);
    }

    /**
     * Tests successful payment processing of an unpaid installment with matching amount.
     */
    @Test
    void testPayInstallment_success() {
        log.info("Test: payInstallment - success case");

        when(installmentRepository.findById(installment.getId())).thenReturn(Optional.of(installment));
        when(installmentRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(lateFeeCalculatorService.calculateLateFeeForInstallment(any())).thenReturn(0.0);

        BNPLInstallmentResponseDTO response = bnplPaymentService.payInstallment(installment.getId(), 500.0);

        assertNotNull(response);
        assertTrue(response.getIsPaid());
        assertEquals(installment.getId(), response.getId());
        verify(installmentRepository, times(1)).save(installment);
        log.info("Payment successfully processed for installment ID: {}", installment.getId());
    }

    /**
     * Tests payment fails if installment is already paid.
     */
    @Test
    void testPayInstallment_alreadyPaid() {
        log.info("Test: payInstallment - already paid case");

        installment.setIsPaid(true);
        when(installmentRepository.findById(installment.getId())).thenReturn(Optional.of(installment));

        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> bnplPaymentService.payInstallment(installment.getId(), 500.0));
        assertEquals("Installment is already paid", ex.getMessage());
        log.info("Correctly threw exception for already paid installment");
    }

    /**
     * Tests payment fails if amount does not match installment amount.
     */
    @Test
    void testPayInstallment_invalidAmount() {
        log.info("Test: payInstallment - invalid amount case");

        when(installmentRepository.findById(installment.getId())).thenReturn(Optional.of(installment));

        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> bnplPaymentService.payInstallment(installment.getId(), 400.0));
        assertEquals("Payment amount must match installment amount", ex.getMessage());
        log.info("Correctly threw exception for invalid payment amount");
    }

    /**
     * Tests retrieval of pending installments by transaction ID.
     */
    @Test
    void testGetPendingInstallmentsByTransactionId() {
        log.info("Test: getPendingInstallmentsByTransactionId");

        when(installmentRepository.findByTransaction_IdAndIsPaidFalse(transaction.getId()))
                .thenReturn(List.of(installment));
        when(lateFeeCalculatorService.calculateLateFeeForInstallment(any())).thenReturn(0.0);

        List<BNPLInstallmentResponseDTO> results = bnplPaymentService.getPendingInstallmentsByTransactionId(transaction.getId());

        assertFalse(results.isEmpty());
        assertEquals(1, results.size());
        assertEquals(installment.getId(), results.get(0).getId());
        log.info("Pending installments retrieval successful for transaction ID: {}", transaction.getId());
    }

    /**
     * Tests creation of a new installment.
     */
    @Test
    void testCreateInstallment_success() {
        log.info("Test: createInstallment");

        BNPLInstallmentCreateDTO createDTO = new BNPLInstallmentCreateDTO();
        createDTO.setTransactionId(transaction.getId());
        createDTO.setInstallmentNumber(1);
        createDTO.setAmount(500.0);
        createDTO.setDueDate(LocalDate.now().plusDays(10));
        createDTO.setIsPaid(false);

        when(transactionRepository.findById(transaction.getId())).thenReturn(Optional.of(transaction));
        when(installmentRepository.save(any())).thenAnswer(i -> {
            BNPLInstallment saved = i.getArgument(0);
            saved.setId(101L);
            return saved;
        });
        when(lateFeeCalculatorService.calculateLateFeeForInstallment(any())).thenReturn(0.0);

        BNPLInstallmentResponseDTO responseDTO = bnplPaymentService.createInstallment(createDTO);

        assertNotNull(responseDTO);
        assertEquals(101L, responseDTO.getId());
        assertEquals(transaction.getId(), responseDTO.getTransactionId());
        log.info("Installment created successfully with ID: {}", responseDTO.getId());
    }

    /**
     * Tests deletion of an existing installment.
     */
    @Test
    void testDeleteInstallment_success() {
        log.info("Test: deleteInstallment");

        when(installmentRepository.findById(installment.getId())).thenReturn(Optional.of(installment));
        doNothing().when(installmentRepository).delete(installment);

        assertDoesNotThrow(() -> bnplPaymentService.deleteInstallment(installment.getId()));
        verify(installmentRepository, times(1)).delete(installment);
        log.info("Installment deleted successfully with ID: {}", installment.getId());
    }

    /**
     * Tests update of an installment.
     */
    @Test
    void testUpdateInstallment_success() {
        log.info("Test: updateInstallment");

        BNPLInstallmentUpdateDTO updateDTO = new BNPLInstallmentUpdateDTO();
        updateDTO.setAmount(600.0);
        updateDTO.setDueDate(LocalDate.now().plusDays(15));
        updateDTO.setIsPaid(true);
        updateDTO.setInstallmentNumber(2);

        when(installmentRepository.findById(installment.getId())).thenReturn(Optional.of(installment));
        when(installmentRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(lateFeeCalculatorService.calculateLateFeeForInstallment(any())).thenReturn(0.0);

        BNPLInstallmentResponseDTO response = bnplPaymentService.updateInstallment(installment.getId(), updateDTO);

        assertNotNull(response);
        assertEquals(updateDTO.getAmount(), response.getAmount());
        assertEquals(updateDTO.getIsPaid(), response.getIsPaid());
        log.info("Installment updated successfully for ID: {}", installment.getId());
    }
}
