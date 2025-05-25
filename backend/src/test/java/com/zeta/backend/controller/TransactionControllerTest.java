package com.zeta.backend.controller;

import com.zeta.backend.dto.TransactionCreateDTO;
import com.zeta.backend.dto.TransactionResponseDTO;
import com.zeta.backend.dto.TransactionUpdateDTO;
import com.zeta.backend.enums.InstallmentPlan;
import com.zeta.backend.exception.BadRequestException;
import com.zeta.backend.exception.CardInactiveException;
import com.zeta.backend.exception.InsufficientCreditLimitException;
import com.zeta.backend.exception.InvalidInstallmentPlanException;
import com.zeta.backend.exception.ResourceNotFoundException;
import com.zeta.backend.repository.CardRepository;
import com.zeta.backend.service.ITransactionService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for TransactionController, verifying REST endpoints for transaction management.
 * Tests creation, retrieval, update, and deletion of transactions using DTOs.
 * Mocks ITransactionService and CardRepository to isolate controller logic.
 */
@Slf4j
class TransactionControllerTest {

    private ITransactionService transactionService;
    private CardRepository cardRepository;
    private TransactionController controller;

    /**
     * Sets up mocks and controller before each test.
     */
    @BeforeEach
    void setup() {
        // Initialize mocks
        transactionService = mock(ITransactionService.class);
        cardRepository = mock(CardRepository.class);
        controller = new TransactionController(transactionService);
        log.info("Initialized TransactionControllerTest with mocked dependencies");
    }

    /**
     * Tests successful creation of a regular (non-BNPL) transaction.
     * Verifies HTTP 200 and correct response dto.
     */
    @Test
    void testCreateRegularTransaction_Success() {
        // Arrange: Prepare input dto and mock response
        TransactionCreateDTO input = new TransactionCreateDTO();
        input.setCardId(1L);
        input.setAmount(500.0);
        input.setCategory("Electronics");
        input.setMerchantName("Tech Store");

        TransactionResponseDTO responseDTO = new TransactionResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setCardId(1L);
        responseDTO.setAmount(500.0);
        responseDTO.setStatus("Completed");
        responseDTO.setBNPL(false);

        when(transactionService.simulateRegularTransaction(input)).thenReturn(responseDTO);

        // Act: Call controller method
        log.info("Testing createRegularTransaction for cardId: {}", input.getCardId());
        ResponseEntity<TransactionResponseDTO> response = controller.createRegularTransaction(input);

        // Assert: Verify response and interactions
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(responseDTO, response.getBody());
        verify(transactionService, times(1)).simulateRegularTransaction(input);
        verifyNoInteractions(cardRepository);
        log.info("createRegularTransaction succeeded with transactionId: {}", responseDTO.getId());
    }

    /**
     * Tests creation of a regular transaction with invalid input (e.g., negative amount).
     * Verifies BadRequestException is thrown.
     */
    @Test
    void testCreateRegularTransaction_InvalidInput() {
        // Arrange: Prepare invalid input dto
        TransactionCreateDTO input = new TransactionCreateDTO();
        input.setCardId(1L);
        input.setAmount(-500.0); // Invalid amount

        when(transactionService.simulateRegularTransaction(input))
                .thenThrow(new BadRequestException("Transaction amount must be positive"));

        // Act & Assert: Verify exception
        log.info("Testing createRegularTransaction with invalid amount");
        assertThrows(BadRequestException.class, () -> controller.createRegularTransaction(input));
        verify(transactionService, times(1)).simulateRegularTransaction(input);
        verifyNoInteractions(cardRepository);
        log.info("BadRequestException thrown as expected");
    }

    /**
     * Tests successful creation of a BNPL transaction with a valid installment plan.
     * Verifies HTTP 200 and correct response dto.
     */
    @Test
    void testCreateBNPLTransaction_Success() {
        // Arrange: Prepare input dto and plan
        TransactionCreateDTO input = new TransactionCreateDTO();
        input.setCardId(1L);
        input.setAmount(1200.0);
        input.setCategory("Furniture");
        input.setMerchantName("Home Depot");
        InstallmentPlan plan = InstallmentPlan.THREE;

        TransactionResponseDTO responseDTO = new TransactionResponseDTO();
        responseDTO.setId(2L);
        responseDTO.setCardId(1L);
        responseDTO.setAmount(1200.0);
        responseDTO.setStatus("Pending");
        responseDTO.setBNPL(true);

        when(transactionService.simulateBNPLTransaction(input, plan)).thenReturn(responseDTO);

        // Act: Call controller method
        log.info("Testing createBNPLTransaction for cardId: {} with plan: {}", input.getCardId(), plan);
        ResponseEntity<TransactionResponseDTO> response = controller.createBNPLTransaction(input, plan);

        // Assert: Verify response and interactions
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(responseDTO, response.getBody());
        verify(transactionService, times(1)).simulateBNPLTransaction(input, plan);
        verifyNoInteractions(cardRepository);
        log.info("createBNPLTransaction succeeded with transactionId: {}", responseDTO.getId());
    }

    /**
     * Tests creation of a BNPL transaction with an invalid plan (TWELVE).
     * Verifies InvalidInstallmentPlanException is thrown.
     */
    @Test
    void testCreateBNPLTransaction_InvalidPlan() {
        // Arrange: Prepare input dto with invalid plan
        TransactionCreateDTO input = new TransactionCreateDTO();
        input.setCardId(1L);
        input.setAmount(1200.0);
        InstallmentPlan plan = InstallmentPlan.TWELVE;

        when(transactionService.simulateBNPLTransaction(input, plan))
                .thenThrow(new InvalidInstallmentPlanException(plan));

        // Act & Assert: Verify exception
        log.info("Testing createBNPLTransaction with invalid plan: {}", plan);
        assertThrows(InvalidInstallmentPlanException.class, () -> controller.createBNPLTransaction(input, plan));
        verify(transactionService, times(1)).simulateBNPLTransaction(input, plan);
        verifyNoInteractions(cardRepository);
        log.info("InvalidInstallmentPlanException thrown as expected");
    }

    /**
     * Tests creation of a BNPL transaction with an inactive card.
     * Verifies CardInactiveException is thrown.
     */
    @Test
    void testCreateBNPLTransaction_CardInactive() {
        // Arrange: Prepare input dto
        TransactionCreateDTO input = new TransactionCreateDTO();
        input.setCardId(1L);
        input.setAmount(1200.0);
        InstallmentPlan plan = InstallmentPlan.SIX;

        when(transactionService.simulateBNPLTransaction(input, plan))
                .thenThrow(new CardInactiveException(1L));

        // Act & Assert: Verify exception
        log.info("Testing createBNPLTransaction with inactive cardId: {}", input.getCardId());
        assertThrows(CardInactiveException.class, () -> controller.createBNPLTransaction(input, plan));
        verify(transactionService, times(1)).simulateBNPLTransaction(input, plan);
        verifyNoInteractions(cardRepository);
        log.info("CardInactiveException thrown as expected");
    }

    /**
     * Tests creation of a BNPL transaction with insufficient credit limit.
     * Verifies InsufficientCreditLimitException is thrown.
     */
    @Test
    void testCreateBNPLTransaction_InsufficientLimit() {
        // Arrange: Prepare input dto
        TransactionCreateDTO input = new TransactionCreateDTO();
        input.setCardId(1L);
        input.setAmount(5000.0);
        InstallmentPlan plan = InstallmentPlan.NINE;

        when(transactionService.simulateBNPLTransaction(input, plan))
                .thenThrow(new InsufficientCreditLimitException(5000.0, 1000.0));

        // Act & Assert: Verify exception
        log.info("Testing createBNPLTransaction with insufficient limit for cardId: {}", input.getCardId());
        assertThrows(InsufficientCreditLimitException.class, () -> controller.createBNPLTransaction(input, plan));
        verify(transactionService, times(1)).simulateBNPLTransaction(input, plan);
        verifyNoInteractions(cardRepository);
        log.info("InsufficientCreditLimitException thrown as expected");
    }

    /**
     * Tests retrieval of transaction history for a card.
     * Verifies HTTP 200 and correct response list.
     */
    @Test
    void testGetTransactionHistory() {
        // Arrange: Prepare mock response
        Long cardId = 5L;
        TransactionResponseDTO responseDTO = new TransactionResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setCardId(cardId);
        List<TransactionResponseDTO> transactions = Collections.singletonList(responseDTO);

        when(transactionService.getTransactionHistoryByCardId(cardId)).thenReturn(transactions);

        // Act: Call controller method
        log.info("Testing getTransactionHistory with cardId: {}", cardId);
        ResponseEntity<List<TransactionResponseDTO>> response = controller.getTransactionHistory(cardId);

        // Assert: Verify response and interactions
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(transactions, response.getBody());
        verify(transactionService, times(1)).getTransactionHistoryByCardId(cardId);
        verifyNoInteractions(cardRepository);
        log.info("getTransactionHistory returned {} transactions", transactions.size());
    }

    /**
     * Tests retrieval of all transactions.
     * Verifies HTTP 200 and correct response list.
     */
    @Test
    void testGetAllTransactions() {
        // Arrange: Prepare mock response
        TransactionResponseDTO responseDTO = new TransactionResponseDTO();
        responseDTO.setId(1L);
        List<TransactionResponseDTO> transactions = Collections.singletonList(responseDTO);

        when(transactionService.getAllTransactions()).thenReturn(transactions);

        // Act: Call controller method
        log.info("Testing getAllTransactions");
        ResponseEntity<List<TransactionResponseDTO>> response = controller.getAllTransactions();

        // Assert: Verify response and interactions
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(transactions, response.getBody());
        verify(transactionService, times(1)).getAllTransactions();
        verifyNoInteractions(cardRepository);
        log.info("getAllTransactions returned {} transactions", transactions.size());
    }

    /**
     * Tests retrieval of a transaction by ID.
     * Verifies HTTP 200 and correct response dto.
     */
    @Test
    void testGetTransactionById_Success() {
        // Arrange: Prepare mock response
        Long id = 1L;
        TransactionResponseDTO responseDTO = new TransactionResponseDTO();
        responseDTO.setId(id);

        when(transactionService.getTransactionById(id)).thenReturn(responseDTO);

        // Act: Call controller method
        log.info("Testing getTransactionById with id: {}", id);
        ResponseEntity<TransactionResponseDTO> response = controller.getTransactionById(id);

        // Assert: Verify response and interactions
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(responseDTO, response.getBody());
        verify(transactionService, times(1)).getTransactionById(id);
        verifyNoInteractions(cardRepository);
        log.info("getTransactionById succeeded");
    }

    /**
     * Tests retrieval of a nonexistent transaction.
     * Verifies ResourceNotFoundException is thrown.
     */
    @Test
    void testGetTransactionById_NotFound() {
        // Arrange: Mock service to throw exception
        Long id = 1L;

        when(transactionService.getTransactionById(id))
                .thenThrow(new ResourceNotFoundException("Transaction not found"));

        // Act & Assert: Verify exception
        log.info("Testing getTransactionById with nonexistent id: {}", id);
        assertThrows(ResourceNotFoundException.class, () -> controller.getTransactionById(id));
        verify(transactionService, times(1)).getTransactionById(id);
        verifyNoInteractions(cardRepository);
        log.info("ResourceNotFoundException thrown as expected");
    }

    /**
     * Tests updating a transaction.
     * Verifies HTTP 200 and correct response dto.
     */
    @Test
    void testUpdateTransaction_Success() {
        // Arrange: Prepare input dto and mock response
        Long id = 1L;
        TransactionUpdateDTO updateDTO = new TransactionUpdateDTO();
        updateDTO.setAmount(600.0);
        updateDTO.setCategory("Updated Category");

        TransactionResponseDTO responseDTO = new TransactionResponseDTO();
        responseDTO.setId(id);
        responseDTO.setAmount(600.0);

        when(transactionService.updateTransaction(id, updateDTO)).thenReturn(responseDTO);

        // Act: Call controller method
        log.info("Testing updateTransaction with id: {}", id);
        ResponseEntity<TransactionResponseDTO> response = controller.updateTransaction(id, updateDTO);

        // Assert: Verify response and interactions
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(responseDTO, response.getBody());
        verify(transactionService, times(1)).updateTransaction(id, updateDTO);
        verifyNoInteractions(cardRepository);
        log.info("updateTransaction succeeded");
    }

    /**
     * Tests updating a nonexistent transaction.
     * Verifies ResourceNotFoundException is thrown.
     */
    @Test
    void testUpdateTransaction_NotFound() {
        // Arrange: Prepare input dto and mock exception
        Long id = 1L;
        TransactionUpdateDTO updateDTO = new TransactionUpdateDTO();
        updateDTO.setAmount(600.0);

        when(transactionService.updateTransaction(id, updateDTO))
                .thenThrow(new ResourceNotFoundException("Transaction not found"));

        // Act & Assert: Verify exception
        log.info("Testing updateTransaction with nonexistent id: {}", id);
        assertThrows(ResourceNotFoundException.class, () -> controller.updateTransaction(id, updateDTO));
        verify(transactionService, times(1)).updateTransaction(id, updateDTO);
        verifyNoInteractions(cardRepository);
        log.info("ResourceNotFoundException thrown as expected");
    }

    /**
     * Tests deleting a transaction.
     * Verifies HTTP 204 and no content response.
     */
    @Test
    void testDeleteTransaction_Success() {
        // Arrange: Mock service for deletion
        Long id = 1L;

        doNothing().when(transactionService).deleteTransaction(id);

        // Act: Call controller method
        log.info("Testing deleteTransaction with id: {}", id);
        ResponseEntity<Void> response = controller.deleteTransaction(id);

        // Assert: Verify response and interactions
        assertEquals(204, response.getStatusCodeValue());
        verify(transactionService, times(1)).deleteTransaction(id);
        verifyNoInteractions(cardRepository);
        log.info("deleteTransaction succeeded");
    }

    /**
     * Tests deleting a nonexistent transaction.
     * Verifies ResourceNotFoundException is thrown.
     */
    @Test
    void testDeleteTransaction_NotFound() {
        // Arrange: Mock service to throw exception
        Long id = 1L;

        doThrow(new ResourceNotFoundException("Transaction not found")).when(transactionService).deleteTransaction(id);

        // Act & Assert: Verify exception
        log.info("Testing deleteTransaction with nonexistent id: {}", id);
        assertThrows(ResourceNotFoundException.class, () -> controller.deleteTransaction(id));
        verify(transactionService, times(1)).deleteTransaction(id);
        verifyNoInteractions(cardRepository);
        log.info("ResourceNotFoundException thrown as expected");
    }
}