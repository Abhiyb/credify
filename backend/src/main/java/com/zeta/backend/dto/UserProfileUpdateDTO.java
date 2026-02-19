package com.zeta.backend.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UserProfileUpdateDTO {

    @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
    private String fullName;

    @Pattern(regexp = "^\\d{10}$", message = "Phone number must be exactly 10 digits")
    private String phone;

    @Size(max = 500, message = "Address cannot exceed 500 characters")
    private String address;

    @PositiveOrZero(message = "Annual income cannot be negative")
    private Double annualIncome;

    // NO email field here — we ignore it even if sent
}