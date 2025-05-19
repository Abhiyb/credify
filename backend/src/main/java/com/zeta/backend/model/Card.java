package com.zeta.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "cards")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cardId;

    @NotNull
    @Size(min = 12, max = 16, message = "Card number must be between 12 and 16 digits")
    @Column(unique = true, nullable = false)
    private String cardNumber;

    @NotNull
    @Column(nullable = false)
    private String cardType; // VISA, Mastercard, etc.

    @NotNull
    @Column(nullable = false)
    private String status; // ACTIVE, BLOCKED, INACTIVE


    @NotNull
    private Double availableLimit;

    @NotNull
    private LocalDate expiryDate;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

}
