package com.zeta.backend.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

/**
 * dto for creating a new transaction, used in POST /transactions.
 * Contains essential fields for transaction creation with validation to ensure
 * required data is provided and meets business rules.
 */
@Data
public class TransactionCreateDTO {
    /**
     * ID of the card associated with the transaction.
     * Must be non-null and a valid card ID.
     */
    @NotNull(message = "Card ID is required")
    private Long cardId;

    /**
     * Transaction amount in INR.
     * Must be positive and non-null to ensure valid transaction value.
     */
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private Double amount;

    /**
     * Category of the transaction (e.g., Electronics, Clothing).
     * Must be non-blank to categorize the transaction properly.
     */
    @NotBlank(message = "Category is required")
    private String category;

    /**
     * Name of the merchant for the transaction.
     * Must be non-blank to identify the merchant involved.
     */
    @NotBlank(message = "Merchant name is required")
    private String merchantName;


    /**
     * Indicates if the transaction is a Buy Now, Pay Later (BNPL) transaction.
     * Defaults to false for regular transactions.
     */
    private boolean isBNPL;
}