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
public class CardDto {
    private Long cardId;
    private String cardNumber;
    private String cardType;
    private String status;
    private double creditLimit;
    private double availableLimit;
    private LocalDate expiryDate;
    private Long userId;
    private Long applicationId;
}
