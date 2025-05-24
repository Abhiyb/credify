package com.zeta.backend.service;

import com.zeta.backend.enums.InstallmentPlan;
import com.zeta.backend.exception.ResourceNotFoundException;
import com.zeta.backend.model.BNPLInstallment;
import com.zeta.backend.model.Card;
import com.zeta.backend.model.Transaction;
import com.zeta.backend.repository.BNPLInstallmentRepository;
import com.zeta.backend.repository.CardRepository;
import com.zeta.backend.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransactionServiceTest {

    @InjectMocks
    private TransactionService transactionService;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private BNPLInstallmentRepository bnplInstallmentRepository;

    @Mock
    private CardRepository cardRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSimulateRegularTransaction_success() {
        Transaction txn = new Transaction();
        txn.setAmount(500.0);
        txn.setCard(new Card());
        txn.getCard().setCardId(1L);
        txn.getCard().setStatus("ACTIVE");
        txn.getCard().setAvailableLimit(1000.0);

        when(cardRepository.findById(1L)).thenReturn(Optional.of(txn.getCard()));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(txn);

        Transaction result = transactionService.simulateRegularTransaction(txn);

        assertFalse(result.getIsBNPL());
        assertEquals("Completed", result.getStatus());
        verify(cardRepository).save(any(Card.class));
        verify(transactionRepository).save(txn);
    }

    @Test
    void testSimulateRegularTransaction_insufficientLimit() {
        Transaction txn = new Transaction();
        txn.setAmount(1500.0);
        Card card = new Card();
        card.setCardId(1L);
        card.setAvailableLimit(1000.0);
        card.setStatus("ACTIVE");
        txn.setCard(card);

        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));

        assertThrows(IllegalStateException.class, () -> transactionService.simulateRegularTransaction(txn));
    }

    @Test
    void testSimulateBNPLTransaction_createsInstallments() {
        Transaction txn = new Transaction();
        txn.setAmount(1200.0);
        Card card = new Card();
        card.setCardId(1L);
        card.setAvailableLimit(2000.0);
        card.setStatus("ACTIVE");
        txn.setCard(card);

        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(txn);

        Transaction result = transactionService.simulateBNPLTransaction(txn, InstallmentPlan.THREE);

        assertTrue(result.getIsBNPL());
        assertEquals("Pending", result.getStatus());
        verify(bnplInstallmentRepository).saveAll(anyList());
    }

    @Test
    void testGetTransactionById_notFound() {
        when(transactionRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> transactionService.getTransactionById(1L));
    }

    @Test
    void testDeleteTransaction_success() {
        Transaction txn = new Transaction();
        txn.setId(1L);
        when(transactionRepository.findById(1L)).thenReturn(Optional.of(txn));
        transactionService.deleteTransaction(1L);
        verify(transactionRepository).delete(txn);
    }
}
