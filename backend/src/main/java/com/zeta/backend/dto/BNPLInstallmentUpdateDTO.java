package com.zeta.backend.dto;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDate;

/**
 * dto for updating an existing BNPL installment, used in PUT /bnpl/installments/{id}.
 * Allows partial updates to installment fields with validation for required fields.
 * Optional fields default to existing values if not provided.
 */
@Data
public class BNPLInstallmentUpdateDTO {
    /**
     * ID of the transaction associated with the installment.
     * Optional, defaults to the existing transaction ID if not provided.
     */
    private Long transactionId;

    /**
     * Sequential number of the installment (e.g., 1, 2, 3).
     * Optional, defaults to the existing installment number if not provided.
     */
    private Integer installmentNumber;

    /**
     * Amount of the installment in INR.
     * Must be positive and non-null if provided to ensure a valid update.
     */
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private Double amount;

    /**
     * Due date for the installment payment.
     * Must be non-null if provided to define when the payment is due.
     */
    @NotNull(message = "Due date is required")
    private LocalDate dueDate;

    /**
     * Indicates if the installment is paid.
     * Optional, defaults to the existing payment status if not provided.
     */
    private Boolean isPaid;
}