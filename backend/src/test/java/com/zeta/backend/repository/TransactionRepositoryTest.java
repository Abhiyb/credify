package com.zeta.backend.repository;

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
 * Unit tests for TransactionRepository using an in-memory database.
 */
@Slf4j
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class TransactionRepositoryTest {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private CardRepository cardRepository;

    private Card card;

    /**
     * Setup a sample card and a couple of transactions before each test case.
     */
    @BeforeEach
    public void setup() {
        log.info("Setting up test data...");

        card = Card.builder()
                .cardNumber("9876543210987654")
                .cardType("MASTER")
                .status("ACTIVE")
                .creditLimit(60000.0)
                .availableLimit(60000.0)
                .expiryDate(LocalDate.now().plusYears(3))
                .build();
        card = cardRepository.save(card);

        Transaction tx1 = new Transaction();
        tx1.setCard(card);
        tx1.setCardId(card.getCardId());
        tx1.setMerchantName("Flipkart");
        tx1.setAmount(5000.0);
        tx1.setTransactionDate(LocalDate.now().minusDays(5));
        tx1.setCategory("Shopping");
        tx1.setIsBNPL(true);
        tx1.setStatus("APPROVED");

        Transaction tx2 = new Transaction();
        tx2.setCard(card);
        tx2.setCardId(card.getCardId());
        tx2.setMerchantName("Walmart");
        tx2.setAmount(3000.0);
        tx2.setTransactionDate(LocalDate.now().minusDays(3));
        tx2.setCategory("Groceries");
        tx2.setIsBNPL(false);
        tx2.setStatus("APPROVED");

        transactionRepository.save(tx1);
        transactionRepository.save(tx2);

        log.info("Test data setup complete.");
    }

    /**
     * Tests countByCardId method to verify total transactions for a card.
     */
    @Test
    public void testCountByCardId() {
        Long count = transactionRepository.countByCardId(card.getCardId());
        log.info("Transaction count for cardId {}: {}", card.getCardId(), count);
        assertThat(count).isEqualTo(2);
    }

    /**
     * Tests findByCardId to fetch all transactions of a specific card.
     */
    @Test
    public void testFindByCardId() {
        List<Transaction> transactions = transactionRepository.findByCardId(card.getCardId());
        log.info("Fetched {} transactions for cardId {}", transactions.size(), card.getCardId());
        assertThat(transactions).hasSize(2);
    }

    /**
     * Tests findByIsBNPLTrue to ensure only BNPL transactions are fetched.
     */
    @Test
    public void testFindByIsBNPLTrue() {
        List<Transaction> bnplTransactions = transactionRepository.findByIsBNPLTrue();
        log.info("Found {} BNPL transactions", bnplTransactions.size());
        assertThat(bnplTransactions).hasSize(1);
        assertThat(bnplTransactions.get(0).getMerchantName()).isEqualTo("Flipkart");
    }
}
