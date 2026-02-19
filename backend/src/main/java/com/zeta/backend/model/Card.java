package com.zeta.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cardId; // Primary key for the Card table, auto-generated

    private String cardNumber;    // Typically a 16-digit unique number
    private String cardType;      // e.g., VISA, MasterCard
    private String status;        // ACTIVE, BLOCKED, etc.
    private double creditLimit;   // Total credit limit assigned
    private double availableLimit; // Remaining credit available
    private LocalDate expiryDate; // Expiration date of the card
    private String cvv;
    private String expiryMonth;
    private String expiryYear;
    @OneToOne
    @JoinColumn(name = "application_id")
    private CardApplication application; // One-to-one link with the card application that created this card

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserProfile user; // Many cards can belong to a single user profile

}
