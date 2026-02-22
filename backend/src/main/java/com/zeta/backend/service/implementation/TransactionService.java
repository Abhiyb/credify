package com.zeta.backend.service.implementation;

import com.zeta.backend.dto.TransactionCreateDTO;
import com.zeta.backend.dto.TransactionResponseDTO;
import com.zeta.backend.dto.TransactionUpdateDTO;
import com.zeta.backend.enums.InstallmentPlan;
import com.zeta.backend.exception.*;
import com.zeta.backend.model.BNPLInstallment;
import com.zeta.backend.model.Card;
import com.zeta.backend.model.Transaction;
import com.zeta.backend.repository.BNPLInstallmentRepository;
import com.zeta.backend.repository.CardRepository;
import com.zeta.backend.repository.TransactionRepository;
import com.zeta.backend.service.ITransactionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionService implements ITransactionService {

    private final TransactionRepository transactionRepository;
    private final BNPLInstallmentRepository bnplInstallmentRepository;
    private final CardRepository cardRepository;

    /**
     * Validate card details for transaction (used by /validate-card)
     */
    @Override
    public boolean validateCardDetails(TransactionCreateDTO dto, String userEmail) {
        log.info("Validating card: number={}, cvv=****, expiry={}/{}, email={}",
                dto.getCardNumber().substring(dto.getCardNumber().length() - 4),
                dto.getExpiryMonth(), dto.getExpiryYear(), userEmail);

        Optional<Card> cardOpt = cardRepository.findByCardNumberAndCvvAndExpiryMonthAndExpiryYearAndUserEmail(
                dto.getCardNumber(),
                dto.getCvv(),
                dto.getExpiryMonth(),
                dto.getExpiryYear(),
                userEmail
        );

        if (cardOpt.isEmpty()) {
            log.warn("Card validation failed - not found or mismatch");
            return false;
        }

        Card card = cardOpt.get();

        if (!"ACTIVE".equalsIgnoreCase(card.getStatus())) {
            log.warn("Card is not active: status={}", card.getStatus());
            return false;
        }

        if (dto.getAmount() != null && dto.getAmount() > card.getAvailableLimit()) {
            log.warn("Amount {} exceeds available limit {}", dto.getAmount(), card.getAvailableLimit());
            return false;
        }

        log.info("Card validation successful");
        return true;
    }

    /**
     * Regular transaction
     */
    @Override
    @Transactional
    public TransactionResponseDTO simulateRegularTransaction(TransactionCreateDTO transaction, String userEmail) {
        validateTransactionAmount(transaction.getAmount());

        Card card = cardRepository.findByCardNumberAndCvvAndExpiryMonthAndExpiryYearAndUserEmail(
                transaction.getCardNumber(),
                transaction.getCvv(),
                transaction.getExpiryMonth(),
                transaction.getExpiryYear(),
                userEmail
        ).orElseThrow(() -> new ResourceNotFoundException("Card not found or invalid card details"));

        validateCard(card, transaction.getAmount());

        card.setAvailableLimit(card.getAvailableLimit() - transaction.getAmount());
        cardRepository.save(card);

        Transaction entity = new Transaction();
        entity.setCard(card);
        entity.setCardId(card.getCardId());
        entity.setAmount(transaction.getAmount());
        entity.setCategory(transaction.getCategory());
        entity.setMerchantName(transaction.getMerchantName());
        entity.setTransactionDate(LocalDate.now());
        entity.setIsBNPL(false);
        entity.setStatus("Completed");

        Transaction saved = transactionRepository.save(entity);
        return mapToResponseDTO(saved);
    }

    /**
     * BNPL transaction
     */
    @Override
    @Transactional
    public TransactionResponseDTO simulateBNPLTransaction(TransactionCreateDTO transaction,
                                                          InstallmentPlan plan,
                                                          String userEmail) {

        validateTransactionAmount(transaction.getAmount());
        validateInstallmentPlan(plan);

        Card card = cardRepository.findByCardNumberAndCvvAndExpiryMonthAndExpiryYearAndUserEmail(
                transaction.getCardNumber(),
                transaction.getCvv(),
                transaction.getExpiryMonth(),
                transaction.getExpiryYear(),
                userEmail
        ).orElseThrow(() -> new ResourceNotFoundException("Card not found or invalid card details"));

        validateCard(card, transaction.getAmount());

        card.setAvailableLimit(card.getAvailableLimit() - transaction.getAmount());
        cardRepository.save(card);

        Transaction entity = new Transaction();
        entity.setCard(card);
        entity.setCardId(card.getCardId());
        entity.setAmount(transaction.getAmount());
        entity.setCategory(transaction.getCategory());
        entity.setMerchantName(transaction.getMerchantName());
        entity.setTransactionDate(LocalDate.now());
        entity.setIsBNPL(true);
        entity.setStatus("Pending");

        Transaction savedTransaction = transactionRepository.save(entity);
        createInstallments(savedTransaction, plan.getMonths());
        return mapToResponseDTO(savedTransaction);
    }

    /**
     * Create BNPL installments
     */
    @Transactional
    private void createInstallments(Transaction transaction, int installmentCount) {
        double totalAmount = transaction.getAmount();
        double installmentAmount = Math.round((totalAmount / installmentCount) * 100.0) / 100.0;
        LocalDate firstDueDate = LocalDate.now().plusMonths(1);
        double totalCreated = 0;
        List<BNPLInstallment> installments = new ArrayList<>();

        for (int i = 1; i <= installmentCount; i++) {
            double amount = (i == installmentCount) ? totalAmount - totalCreated : installmentAmount;
            totalCreated += amount;

            BNPLInstallment installment = BNPLInstallment.builder()
                    .transaction(transaction)
                    .installmentNumber(i)
                    .amount(amount)
                    .dueDate(firstDueDate.plusMonths(i - 1))
                    .isPaid(false)
                    .build();

            installments.add(installment);
        }

        bnplInstallmentRepository.saveAll(installments);
    }

    /**
     * Get transaction history by card
     */
    @Override
    public List<TransactionResponseDTO> getTransactionHistoryByCardId(Long cardId, String userEmail) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new ResourceNotFoundException("Card not found"));
        if (!card.getUser().getEmail().equals(userEmail)) {
            throw new UnauthorizedException("You do not own this card");
        }
        List<Transaction> transactions = transactionRepository.findByCardId(cardId);
        return transactions.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get transaction by ID
     */
    @Override
    public TransactionResponseDTO getTransactionById(Long id, String userEmail) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));

        if (!transaction.getCard().getUser().getEmail().equals(userEmail)) {
            throw new UnauthorizedException("You cannot access this transaction");
        }
        return mapToResponseDTO(transaction);
    }

    @Override
    public List<TransactionResponseDTO> getAllTransactions() {
        List<Transaction> transactions = transactionRepository.findAll();
        return transactions.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public TransactionResponseDTO updateTransaction(Long id, TransactionUpdateDTO updatedTransaction, String userEmail) {
        Transaction existing = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));

        if (!existing.getCard().getUser().getEmail().equals(userEmail)) {
            throw new UnauthorizedException("You cannot update this transaction");
        }

        existing.setAmount(updatedTransaction.getAmount());
        existing.setCategory(updatedTransaction.getCategory());
        existing.setMerchantName(updatedTransaction.getMerchantName());
        existing.setTransactionDate(updatedTransaction.getTransactionDate() != null
                ? updatedTransaction.getTransactionDate() : LocalDate.now());
        existing.setStatus(updatedTransaction.getStatus() != null
                ? updatedTransaction.getStatus() : existing.getStatus());
        existing.setIsBNPL(updatedTransaction.isBNPL());

        Transaction saved = transactionRepository.save(existing);
        return mapToResponseDTO(saved);
    }

    @Override
    @Transactional
    public void deleteTransaction(Long id, String userEmail) {
        Transaction existing = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));

        if (!existing.getCard().getUser().getEmail().equals(userEmail)) {
            throw new UnauthorizedException("You cannot delete this transaction");
        }

        transactionRepository.delete(existing);
    }

    private TransactionResponseDTO mapToResponseDTO(Transaction transaction) {
        TransactionResponseDTO dto = new TransactionResponseDTO();
        dto.setId(transaction.getId());
        dto.setCardId(transaction.getCardId());
        dto.setAmount(transaction.getAmount());
        dto.setCategory(transaction.getCategory());
        dto.setMerchantName(transaction.getMerchantName());
        dto.setTransactionDate(transaction.getTransactionDate());
        dto.setStatus(transaction.getStatus());
        dto.setBNPL(transaction.getIsBNPL());
        return dto;
    }

    private void validateTransactionAmount(Double amount) {
        if (amount == null || amount <= 0) {
            throw new BadRequestException("Transaction amount must be positive");
        }
    }

    private void validateCard(Card card, Double amount) {
        if (!"ACTIVE".equalsIgnoreCase(card.getStatus())) {
            throw new CardInactiveException(card.getCardId());
        }
        if (amount > card.getAvailableLimit()) {
            throw new InsufficientCreditLimitException(amount, card.getAvailableLimit());
        }
    }

    private void validateInstallmentPlan(InstallmentPlan plan) {
        if (plan == InstallmentPlan.TWELVE) {
            throw new InvalidInstallmentPlanException(plan);
        }
    }
}