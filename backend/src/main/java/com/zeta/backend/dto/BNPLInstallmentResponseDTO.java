package com.zeta.backend.dto;


import lombok.Data;

import java.time.LocalDate;

/**
 * dto for returning BNPL installment details to the frontend.
 * Used in responses for GET /bnpl/installments, GET /bnpl/installments/transaction/{transactionId},
 * and other endpoints. Includes all relevant fields for displaying installment information,
 * including a computed late fee for overdue payments.
 */
@Data
public class BNPLInstallmentResponseDTO {
    /**
     * Unique ID of the installment.
     * Used to identify the installment in the system.
     */
    private Long id;

    /**
     * ID of the transaction associated with the installment.
     * Links the installment to its parent transaction.
     */
    private Long transactionId;

    /**
     * Sequential number of the installment (e.g., 1, 2, 3).
     * Indicates the order in the installment plan.
     */
    private Integer installmentNumber;

    /**
     * Amount of the installment in INR.
     * Represents the payment amount for this installment.
     */
    private Double amount;

    /**
     * Due date for the installment payment.
     * Specifies when the payment is due.
     */
    private LocalDate dueDate;

    /**
     * Indicates if the installment is paid.
     * True if paid, false if pending or overdue.
     */
    private Boolean isPaid;

    /**
     * Late fee for the installment, computed by LateFeeCalculatorService.
     * Zero if the installment is not overdue or has no penalties.
     */
    private Double lateFee;
}
