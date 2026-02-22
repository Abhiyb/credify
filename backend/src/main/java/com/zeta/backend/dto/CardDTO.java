package com.zeta.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardDTO {

    private Long cardId;             // Unique identifier of the card

    private String cardNumber;       // Masked or full card number, depending on business rules

    private String cardType;         // Card type such as VISA, MasterCard, etc.

    private String status;           // Current status of the card - ACTIVE, BLOCKED, etc.

    private Double creditLimit;      // Total credit limit allocated to the card

    private Double availableLimit;   // Remaining usable limit after transactions

    private LocalDate expiryDate;    // Expiration date of the card

    private String cardHolderName;   // Full name of the person to whom the card is issued

    private String cvv;   // ← Add this field
}
