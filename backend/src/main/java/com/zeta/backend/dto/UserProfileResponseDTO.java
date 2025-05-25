package com.zeta.backend.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileResponseDTO {
    private Long userId;
    private String fullName;
    private String email;
    private String phone;
    private String address;
    private Double annualIncome;
    private Boolean isEligibleForBNPL;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}