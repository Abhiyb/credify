package com.zeta.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "cards")
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cardId;

    @NotNull
    @Size(min = 12, max = 16)
    @Column(unique = true, nullable = false)
    private String cardNumber;

    @NotNull
    @Column(nullable = false)
    private Long userId;  // assuming userId is a foreign key reference

    @NotNull
    @Column(nullable = false)
    private String cardType;  // e.g., "VISA", "MasterCard"

    @NotNull
    @Column(nullable = false)
    private String status;  // e.g., "ACTIVE", "BLOCKED"

    // Constructors
    public Card() {
    }

    public Card(String cardNumber, Long userId, String cardType, String status) {
        this.cardNumber = cardNumber;
        this.userId = userId;
        this.cardType = cardType;
        this.status = status;
    }

    // Getters and Setters

    public Long getCardId() {
        return cardId;
    }

    public void setCardId(Long cardId) {
        this.cardId = cardId;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
