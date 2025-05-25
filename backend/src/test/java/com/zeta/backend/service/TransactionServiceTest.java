package com.zeta.backend.service;

import com.zeta.backend.dto.TransactionCreateDTO;
import com.zeta.backend.dto.TransactionResponseDTO;
import com.zeta.backend.enums.InstallmentPlan;
import com.zeta.backend.exception.*;
import com.zeta.backend.model.Card;
import com.zeta.backend.model.Transaction;
import com.zeta.backend.repository.BNPLInstallmentRepository;
import com.zeta.backend.repository.CardRepository;
import com.zeta.backend.repository.TransactionRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Slf4j
public class TransactionServiceTest {

    @InjectMocks
    private TransactionService transactionService;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private CardRepository cardRepository;

    @Mock
    private BNPLInstallmentRepository bnplInstallmentRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Test successful regular transaction simulation.
     */
    @Test
    void testSimulateRegularTransaction_Success() {
        log.info("Running testSimulateRegularTransaction_Success");

        Card card = new Card();
        card.setCardId(1L);
        card.setStatus("ACTIVE");
        card.setAvailableLimit(5000.0);

        TransactionCreateDTO dto = new TransactionCreateDTO();
        dto.setCardId(1L);
        dto.setAmount(1000.0);
        dto.setCategory("Shopping");
        dto.setMerchantName("Amazon");

        Transaction saved = new Transaction();
        saved.setId(1L);
        saved.setAmount(1000.0);
        saved.setCard(card);
        saved.setTransactionDate(LocalDate.now());

        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
        when(cardRepository.save(any())).thenReturn(card);
        when(transactionRepository.save(any())).thenReturn(saved);

        TransactionResponseDTO response = transactionService.simulateRegularTransaction(dto);

        assertEquals(1000.0, response.getAmount());
        verify(transactionRepository, times(1)).save(any());
        log.info("Completed testSimulateRegularTransaction_Success");
    }

    /**
     * Test regular transaction with insufficient credit.
     */
    @Test
    void testSimulateRegularTransaction_InsufficientCredit() {
        log.info("Running testSimulateRegularTransaction_InsufficientCredit");

        Card card = new Card();
        card.setCardId(2L);
        card.setStatus("ACTIVE");
        card.setAvailableLimit(500.0);

        TransactionCreateDTO dto = new TransactionCreateDTO();
        dto.setCardId(2L);
        dto.setAmount(1000.0);

        when(cardRepository.findById(2L)).thenReturn(Optional.of(card));

        assertThrows(InsufficientCreditLimitException.class, () -> {
            transactionService.simulateRegularTransaction(dto);
        });

        log.info("Completed testSimulateRegularTransaction_InsufficientCredit");
    }

    /**
     * Test BNPL transaction with invalid plan.
     */
    @Test
    void testSimulateBNPLTransaction_InvalidPlan() {
        log.info("Running testSimulateBNPLTransaction_InvalidPlan");

        TransactionCreateDTO dto = new TransactionCreateDTO();
        dto.setCardId(3L);
        dto.setAmount(1200.0);

        assertThrows(InvalidInstallmentPlanException.class, () -> {
            transactionService.simulateBNPLTransaction(dto, InstallmentPlan.TWELVE);
        });

        log.info("Completed testSimulateBNPLTransaction_InvalidPlan");
    }

    /**
     * Test get transaction history by card ID.
     */
    @Test
    void testGetTransactionHistoryByCardId() {
        log.info("Running testGetTransactionHistoryByCardId");

        Transaction tx = new Transaction();
        tx.setId(1L);
        tx.setAmount(200.0);
        tx.setTransactionDate(LocalDate.now());

        when(transactionRepository.findByCardId(1L)).thenReturn(List.of(tx));

        List<TransactionResponseDTO> result = transactionService.getTransactionHistoryByCardId(1L);

        assertEquals(1, result.size());
        assertEquals(200.0, result.get(0).getAmount());

        log.info("Completed testGetTransactionHistoryByCardId");
    }

    /**
     * Test get transaction by ID throws exception if not found.
     */
    @Test
    void testGetTransactionById_NotFound() {
        log.info("Running testGetTransactionById_NotFound");

        when(transactionRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            transactionService.getTransactionById(999L);
        });

        log.info("Completed testGetTransactionById_NotFound");
    }
}
