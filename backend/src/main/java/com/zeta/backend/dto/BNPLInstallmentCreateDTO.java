package com.zeta.backend.dto;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDate;

/**
 * dto for creating a new BNPL installment, used in POST /bnpl/installments.
 * Contains required fields for installment creation with validation to ensure
 * data integrity and association with a valid transaction.
 */
@Data
public class BNPLInstallmentCreateDTO {
    /**
     * ID of the transaction associated with the installment.
     * Must be non-null and correspond to an existing transaction.
     */
    @NotNull(message = "Transaction ID is required")
    private Long transactionId;

    /**
     * Sequential number of the installment (e.g., 1, 2, 3).
     * Must be non-null to maintain order in the installment plan.
     */
    @NotNull(message = "Installment number is required")
    private Integer installmentNumber;

    /**
     * Amount of the installment in INR.
     * Must be positive and non-null to ensure a valid payment amount.
     */
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private Double amount;

    /**
     * Due date for the installment payment.
     * Must be non-null to define when the payment is due.
     */
    @NotNull(message = "Due date is required")
    private LocalDate dueDate;

    /**
     * Indicates if the installment is paid.
     * Defaults to false for new installments, as they are unpaid by default.
     */
    private Boolean isPaid = false;
}
