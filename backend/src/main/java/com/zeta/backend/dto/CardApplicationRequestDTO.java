package com.zeta.backend.dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardApplicationRequestDTO {
    private Long userId;
    private String cardType;
    private Double annualIncome;
    private Double requestedLimit;
}
