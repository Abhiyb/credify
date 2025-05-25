package com.zeta.backend.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDate;

/**
 * dto for updating an existing transaction, used in PUT /transactions/{id}.
 * Allows partial updates to transaction fields with validation for required fields.
 * Optional fields default to existing values if not provided.
 */
@Data
public class TransactionUpdateDTO {
    /**
     * Transaction amount in INR.
     * Must be positive and non-null to ensure a valid update.
     */
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private Double amount;

    /**
     * Category of the transaction (e.g., Electronics, Clothing).
     * Must be non-blank to maintain proper categorization.
     */
    @NotBlank(message = "Category is required")
    private String category;

    /**
     * Name of the merchant for the transaction.
     * Must be non-blank to identify the merchant.
     */
    @NotBlank(message = "Merchant name is required")
    private String merchantName;

    /**
     * Date the transaction was created.
     * Optional, defaults to the existing transaction date if not provided.
     */
    private LocalDate transactionDate;

    /**
     * Status of the transaction (e.g., Completed, Pending).
     * Optional, defaults to the existing status if not provided.
     */
    private String status;

    /**
     * Indicates if the transaction is a Buy Now, Pay Later (BNPL) transaction.
     * Optional, defaults to the existing value if not provided.
     */
    private boolean isBNPL;
}
