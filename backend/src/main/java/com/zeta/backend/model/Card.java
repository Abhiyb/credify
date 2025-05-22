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
    private Long cardId;

    private String cardNumber;
    private String cardType;
    private String status;
    private double creditLimit;
    private double availableLimit;
    private LocalDate expiryDate;

    @OneToOne
    @JoinColumn(name = "application_id")
    private CardApplication application;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserProfile user;
}
