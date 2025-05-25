package com.zeta.backend.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileUpdateDTO {
    private String fullName;

    @Pattern(
            regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$",
            message = "Email should be valid",
            groups = {ValidationGroups.Update.class}
    )
    private String email;

    @Pattern(
            regexp = "^\\d{10}$",
            message = "Phone number must be 10 digits",
            groups = {ValidationGroups.Update.class}
    )
    private String phone;

    private String address;

    @Positive(
            message = "Annual income must be greater than 0",
            groups = {ValidationGroups.Update.class}
    )
    private Double annualIncome;

    private Boolean isEligibleForBNPL;

    interface ValidationGroups {
        interface Update {}
    }
}

