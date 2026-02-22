package com.zeta.backend.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

/**
 * DTO for creating a new transaction (regular or BNPL).
 * Users provide card number, CVV, expiry instead of internal cardId.
 */
@Data
public class TransactionCreateDTO {

    @NotBlank(message = "Card number is required")
    @Pattern(regexp = "^\\d{15,16}$", message = "Card number must be 15 or 16 digits")
    private String cardNumber;

    @NotBlank(message = "CVV is required")
    @Pattern(regexp = "^\\d{3,4}$", message = "CVV must be 3 or 4 digits")
    private String cvv;

    @NotBlank(message = "Expiry month is required")
    @Pattern(regexp = "^(0[1-9]|1[0-2])$", message = "Expiry month must be between 01 and 12")
    private String expiryMonth;

    @NotBlank(message = "Expiry year is required")
    @Pattern(regexp = "^\\d{2,4}$", message = "Expiry year must be 2 or 4 digits")
    private String expiryYear;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private Double amount;

    @NotBlank(message = "Category is required")
    private String category;

    @NotBlank(message = "Merchant name is required")
    private String merchantName;

    private boolean isBNPL = false;
}