package com.zeta.backend.controller;

import com.zeta.backend.enums.InstallmentPlan;
import com.zeta.backend.exception.ResourceNotFoundException;
import com.zeta.backend.model.Card;
import com.zeta.backend.model.Transaction;
import com.zeta.backend.repository.CardRepository;
import com.zeta.backend.service.ITransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TransactionControllerTest {

    private ITransactionService transactionService;
    private CardRepository cardRepository;
    private TransactionController controller;

    @BeforeEach
    public void setup() {
        transactionService = mock(ITransactionService.class);
        cardRepository = mock(CardRepository.class);
        controller = new TransactionController(transactionService, cardRepository);
    }

    @Test
    public void testSimulateTransaction_regular() {
        Transaction txnInput = new Transaction();
        txnInput.setIsBNPL(false);

        Transaction savedTxn = new Transaction();
        savedTxn.setId(1L);
        when(transactionService.simulateRegularTransaction(txnInput)).thenReturn(savedTxn);

        ResponseEntity<Transaction> response = controller.simulateTransaction(txnInput, InstallmentPlan.THREE);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(savedTxn, response.getBody());
        verify(transactionService, times(1)).simulateRegularTransaction(txnInput);
        verify(transactionService, never()).simulateBNPLTransaction(any(), any());
    }

    @Test
    public void testSimulateTransaction_bnpl_withCardId() {
        Card card = new Card();
        card.setCardId(10L);

        Transaction txnInput = new Transaction();
        txnInput.setIsBNPL(true);
        txnInput.setCardId(10L);

        when(cardRepository.findById(10L)).thenReturn(Optional.of(card));
        when(transactionService.simulateBNPLTransaction(any(Transaction.class), eq(InstallmentPlan.THREE)))
                .thenAnswer(invocation -> {
                    Transaction t = invocation.getArgument(0);
                    t.setId(2L);
                    return t;
                });

        ResponseEntity<Transaction> response = controller.simulateTransaction(txnInput, InstallmentPlan.THREE);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(2L, response.getBody().getId());
        assertEquals(card, response.getBody().getCard());

        verify(cardRepository, times(1)).findById(10L);
        verify(transactionService, times(1)).simulateBNPLTransaction(any(), eq(InstallmentPlan.THREE));
    }

    @Test
    public void testSimulateTransaction_bnpl_cardNotFound() {
        Transaction txnInput = new Transaction();
        txnInput.setIsBNPL(true);
        txnInput.setCardId(99L);

        when(cardRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            controller.simulateTransaction(txnInput, InstallmentPlan.THREE);
        });
        verify(transactionService, never()).simulateBNPLTransaction(any(), any());
    }

    @Test
    public void testGetAllTransactions() {
        List<Transaction> transactions = Collections.singletonList(new Transaction());
        when(transactionService.getAllTransactions()).thenReturn(transactions);

        ResponseEntity<List<Transaction>> response = controller.getAllTransactions();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(transactions, response.getBody());
    }

    @Test
    public void testGetTransactionById() {
        Transaction txn = new Transaction();
        txn.setId(1L);
        when(transactionService.getTransactionById(1L)).thenReturn(txn);

        ResponseEntity<Transaction> response = controller.getTransactionById(1L);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(txn, response.getBody());
    }

    @Test
    public void testGetTransactionsByCardId() {
        List<Transaction> txns = Collections.singletonList(new Transaction());
        when(transactionService.getTransactionHistoryByCardId(5L)).thenReturn(txns);

        ResponseEntity<List<Transaction>> response = controller.getTransactionsByCardId(5L);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(txns, response.getBody());
    }

    @Test
    public void testUpdateTransaction() {
        Transaction updated = new Transaction();
        updated.setId(1L);
        when(transactionService.updateTransaction(eq(1L), any(Transaction.class))).thenReturn(updated);

        ResponseEntity<Transaction> response = controller.updateTransaction(1L, new Transaction());

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(updated, response.getBody());
    }

    @Test
    public void testDeleteTransaction() {
        doNothing().when(transactionService).deleteTransaction(1L);

        ResponseEntity<Void> response = controller.deleteTransaction(1L);

        assertEquals(204, response.getStatusCodeValue());
        verify(transactionService, times(1)).deleteTransaction(1L);
    }
}
