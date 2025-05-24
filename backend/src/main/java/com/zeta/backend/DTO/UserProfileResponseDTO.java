package com.zeta.backend.DTO;

import lombok.*;

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
}