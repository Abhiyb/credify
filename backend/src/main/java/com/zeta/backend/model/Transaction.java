package com.zeta.backend.model;

import com.zeta.backend.exception.ResourceNotFoundException;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
@Entity
@Table(name = "transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "card_id", nullable = false, insertable = false, updatable = false)
    private Long cardId;

    @Column(name = "merchant_name")
    private String merchantName;

    @Column(name = "amount", nullable = false)
    private Double amount;

    @Column(name = "transaction_date")
    private LocalDate transactionDate;

    @Column(name = "category")
    private String category;

    @Column(name = "is_bnpl")
    private Boolean isBNPL = false;

    @Column(name = "status")
    private String status;


    @ManyToOne
    @JoinColumn(name = "card_id")
    private Card card;
}


