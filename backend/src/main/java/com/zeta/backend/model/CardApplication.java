package com.zeta.backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "card_applications")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CardApplication {

    @Id
    @GeneratedValue()
    private Long id;
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private UserProfile user;

    private String cardType;

    private Double requestedLimit;

    private LocalDate applicationDate;

    private String status;
    public void setApplicationId(Long applicationId) {
        this.id = applicationId;
    }
}
