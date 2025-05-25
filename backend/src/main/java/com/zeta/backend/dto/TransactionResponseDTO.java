package com.zeta.backend.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * dto for returning transaction details to the frontend.
 * Used in responses for GET /transactions, GET /transactions/{id}, and
 * GET /transactions/card/{cardId} endpoints. Includes all relevant fields
 * for displaying transaction information in a user-friendly format.
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TransactionResponseDTO {
    /**
     * Unique ID of the transaction.
     * Used to identify the transaction in the system.
     */
    private Long id;

    /**
     * ID of the card associated with the transaction.
     * Links the transaction to a specific card.
     */
    private Long cardId;

    /**
     * Transaction amount in INR.
     * Represents the total cost of the transaction.
     */
    private Double amount;

    /**
     * Category of the transaction (e.g., Electronics, Clothing).
     * Helps categorize the transaction for reporting and display.
     */
    private String category;

    /**
     * Name of the merchant for the transaction.
     * Identifies the merchant involved in the transaction.
     */
    private String merchantName;

    /**
     * Date the transaction was created.
     * Indicates when the transaction occurred.
     */
    private LocalDate transactionDate;

    /**
     * Status of the transaction (e.g., Completed, Pending).
     * Reflects the current state of the transaction.
     */
    private String status;

    /**
     * Indicates if the transaction is a Buy Now, Pay Later (BNPL) transaction.
     * True for BNPL transactions, false for regular transactions.
     */
    private boolean isBNPL;
}