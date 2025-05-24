package com.zeta.backend.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardApplicationResponseDto {
    private String cardType;
    private String status;
    private Double requestedLimit;
    private LocalDate applicationDate;
}
