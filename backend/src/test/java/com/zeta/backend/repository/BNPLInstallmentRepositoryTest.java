package com.zeta.backend.repository;

import com.zeta.backend.model.BNPLInstallment;
import com.zeta.backend.model.Card;
import com.zeta.backend.model.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for BNPLInstallmentRepository.
 */
@Slf4j
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class BNPLInstallmentRepositoryTest {

    @Autowired
    private BNPLInstallmentRepository installmentRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private CardRepository cardRepository;

    private Card card;
    private Transaction transaction;

    /**
     * Sets up a card, a transaction, and multiple installments before each test.
     */
    @BeforeEach
    public void setup() {
        log.info("Setting up test data...");

        card = Card.builder()
                .cardNumber("1234567890123456")
                .cardType("VISA")
                .status("ACTIVE")
                .creditLimit(50000.0)
                .availableLimit(40000.0)
                .expiryDate(LocalDate.now().plusYears(2))
                .build();
        card = cardRepository.save(card);

        transaction = new Transaction();
        transaction.setCard(card);
        transaction.setCardId(card.getCardId());
        transaction.setMerchantName("Amazon");
        transaction.setAmount(10000.0);
        transaction.setTransactionDate(LocalDate.now().minusDays(10));
        transaction.setCategory("Electronics");
        transaction.setIsBNPL(true);
        transaction.setStatus("APPROVED");
        transaction = transactionRepository.save(transaction);

        installmentRepository.saveAll(List.of(
                BNPLInstallment.builder()
                        .transaction(transaction)
                        .installmentNumber(1)
                        .amount(2500.0)
                        .dueDate(LocalDate.now().minusDays(1)) // overdue
                        .isPaid(false)
                        .build(),
                BNPLInstallment.builder()
                        .transaction(transaction)
                        .installmentNumber(2)
                        .amount(2500.0)
                        .dueDate(LocalDate.now().plusDays(10))
                        .isPaid(false)
                        .build(),
                BNPLInstallment.builder()
                        .transaction(transaction)
                        .installmentNumber(3)
                        .amount(2500.0)
                        .dueDate(LocalDate.now().plusDays(20))
                        .isPaid(true)
                        .build()
        ));

        log.info("Test data setup complete.");
    }

    /**
     * Validates that all installments are correctly retrieved by transaction ID.
     */
    @Test
    public void testFindByTransactionId() {
        List<BNPLInstallment> list = installmentRepository.findByTransactionId(transaction.getId());
        log.info("Installments for transaction ID {}: {}", transaction.getId(), list.size());
        assertThat(list).hasSize(3);
    }

    /**
     * Validates fetching unpaid installments (ordered by installment number).
     */
    @Test
    public void testGetNextUnpaidInstallment() {
        List<BNPLInstallment> list = installmentRepository.getNextUnpaidInstallment(transaction.getId());
        log.info("Unpaid installments: {}", list.size());
        assertThat(list).hasSize(2);
        assertThat(list.get(0).getInstallmentNumber()).isEqualTo(1);
    }

    /**
     * Checks if unpaid installments can be fetched using a custom method.
     */
    @Test
    public void testFindUnpaidByTransactionId() {
        List<BNPLInstallment> list = installmentRepository.findUnpaidByTransactionId(transaction.getId());
        log.info("Unpaid installments (alt query): {}", list.size());
        assertThat(list).hasSize(2);
    }

    /**
     * Checks if overdue unpaid installments are detected correctly by card ID.
     */
    @Test
    public void testFindOverdueByCardId() {
        List<BNPLInstallment> list = installmentRepository.findOverdueByCardId(card.getCardId(), LocalDate.now());
        log.info("Overdue installments: {}", list.size());
        assertThat(list).hasSize(1);
        assertThat(list.get(0).getInstallmentNumber()).isEqualTo(1);
    }

    /**
     * Checks if the correct unpaid installments are fetched using a nested property path.
     */
    @Test
    public void testFindByTransaction_IdAndIsPaidFalse() {
        List<BNPLInstallment> list = installmentRepository.findByTransaction_IdAndIsPaidFalse(transaction.getId());
        log.info("Installments with isPaid=false: {}", list.size());
        assertThat(list).hasSize(2);
    }

    /**
     * Validates fetching unpaid overdue installments using nested property conditions.
     */
    @Test
    public void testFindByTransaction_Card_CardIdAndIsPaidFalseAndDueDateBefore() {
        List<BNPLInstallment> list = installmentRepository
                .findByTransaction_Card_CardIdAndIsPaidFalseAndDueDateBefore(card.getCardId(), LocalDate.now());
        log.info("Overdue & unpaid installments for cardId {}: {}", card.getCardId(), list.size());
        assertThat(list).hasSize(1);
    }
}
