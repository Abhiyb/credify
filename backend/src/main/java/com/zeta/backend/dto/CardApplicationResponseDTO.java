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
public class CardApplicationResponseDTO {
    private String cardType;
    private String status;
    private Double requestedLimit;
    private LocalDate applicationDate;
}
