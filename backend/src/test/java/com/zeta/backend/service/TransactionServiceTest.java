package com.zeta.backend.service;

import com.zeta.backend.enums.InstallmentPlan;
import com.zeta.backend.exception.ResourceNotFoundException;
import com.zeta.backend.model.BNPLInstallment;
import com.zeta.backend.model.Card;
import com.zeta.backend.model.Transaction;
import com.zeta.backend.repository.BNPLInstallmentRepository;
import com.zeta.backend.repository.CardRepository;
import com.zeta.backend.repository.TransactionRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Slf4j
class TransactionServiceTest {

    @InjectMocks
    private TransactionService transactionService;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private BNPLInstallmentRepository bnplInstallmentRepository;

    @Mock
    private CardRepository cardRepository;

    /**
     * Initialize mocks before each test and log start of test suite.
     */
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        log.info("Starting a test...");
    }

    /**
     * Test simulating a successful regular transaction with sufficient card limit.
     */
    @Test
    void testSimulateRegularTransaction_success() {
        log.info("Running testSimulateRegularTransaction_success");

        // Setup transaction and card with sufficient limit
        Transaction txn = new Transaction();
        txn.setAmount(500.0);
        txn.setCard(new Card());
        txn.getCard().setCardId(1L);
        txn.getCard().setStatus("ACTIVE");
        txn.getCard().setAvailableLimit(1000.0);

        when(cardRepository.findById(1L)).thenReturn(Optional.of(txn.getCard()));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(txn);

        // Call the service method
        Transaction result = transactionService.simulateRegularTransaction(txn);

        // Verify transaction and card state
        assertFalse(result.getIsBNPL());
        assertEquals("Completed", result.getStatus());
        verify(cardRepository).save(any(Card.class));
        verify(transactionRepository).save(txn);

        log.info("Completed testSimulateRegularTransaction_success successfully");
    }

    /**
     * Test simulating a regular transaction when card limit is insufficient, expecting exception.
     */
    @Test
    void testSimulateRegularTransaction_insufficientLimit() {
        log.info("Running testSimulateRegularTransaction_insufficientLimit");

        Transaction txn = new Transaction();
        txn.setAmount(1500.0);
        Card card = new Card();
        card.setCardId(1L);
        card.setAvailableLimit(1000.0);
        card.setStatus("ACTIVE");
        txn.setCard(card);

        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));

        // Expect exception due to insufficient limit
        assertThrows(IllegalStateException.class, () -> transactionService.simulateRegularTransaction(txn));

        log.info("Completed testSimulateRegularTransaction_insufficientLimit successfully");
    }

    /**
     * Test simulating a BNPL transaction that creates installments successfully.
     */
    @Test
    void testSimulateBNPLTransaction_createsInstallments() {
        log.info("Running testSimulateBNPLTransaction_createsInstallments");

        Transaction txn = new Transaction();
        txn.setAmount(1200.0);
        Card card = new Card();
        card.setCardId(1L);
        card.setAvailableLimit(2000.0);
        card.setStatus("ACTIVE");
        txn.setCard(card);

        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(txn);

        // Simulate BNPL transaction with 3-installment plan
        Transaction result = transactionService.simulateBNPLTransaction(txn, InstallmentPlan.THREE);

        // Validate BNPL flags and status
        assertTrue(result.getIsBNPL());
        assertEquals("Pending", result.getStatus());
        verify(bnplInstallmentRepository).saveAll(anyList());

        log.info("Completed testSimulateBNPLTransaction_createsInstallments successfully");
    }

    /**
     * Test getting a transaction by ID when not found, expecting ResourceNotFoundException.
     */
    @Test
    void testGetTransactionById_notFound() {
        log.info("Running testGetTransactionById_notFound");

        when(transactionRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> transactionService.getTransactionById(1L));

        log.info("Completed testGetTransactionById_notFound successfully");
    }

    /**
     * Test deleting a transaction successfully by ID.
     */
    @Test
    void testDeleteTransaction_success() {
        log.info("Running testDeleteTransaction_success");

        Transaction txn = new Transaction();
        txn.setId(1L);
        when(transactionRepository.findById(1L)).thenReturn(Optional.of(txn));

        transactionService.deleteTransaction(1L);

        verify(transactionRepository).delete(txn);

        log.info("Completed testDeleteTransaction_success successfully");
    }
}
