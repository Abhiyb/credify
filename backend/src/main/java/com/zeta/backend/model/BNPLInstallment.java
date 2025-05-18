package com.zeta.backend.model;

import com.zeta.backend.model.Transaction;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "bnpl_installments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BNPLInstallment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_id", nullable = false)
    private Transaction transaction;

    @Column(name = "installment_number", nullable = false)
    private Integer installmentNumber;

    @Column(name = "amount", nullable = false)
    private Double amount;  // <-- Important for installment payment amount

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Column(name = "is_paid", nullable = false)
    private Boolean isPaid;
}

