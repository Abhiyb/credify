package com.zeta.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Slf4j  // Lombok annotation to enable SLF4J logger
public class UserProfile {

    @Id
    @GeneratedValue
    @Column(name = "user_id")
    private Long userId;

    @NotBlank(message = "Full name is required")
    @Column(name = "full_name", nullable = false)
    private String fullName;

    @NotBlank(message = "Email is required")
    @Pattern(
            regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$",
            message = "Email should be valid"
    )
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\d{10}$", message = "Phone number must be 10 digits")
    @Column(nullable = false, unique = true)
    private String phone;

    @NotBlank(message = "Address is required")
    @Column(nullable = false)
    private String address;

    @NotNull(message = "Annual income is required")
    @Positive(message = "Annual income must be greater than 0")
    @Column(name = "annual_income", nullable = false)
    private Double annualIncome;

    @Column(nullable = false)
    private String password;

    @Column(name = "is_eligible_for_bnpl", nullable = false)
    private Boolean isEligibleForBNPL;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Set creation timestamp and evaluate BNPL eligibility before persisting
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.isEligibleForBNPL = evaluateEligibility();
        log.info("UserProfile created: userId={}, eligibleForBNPL={}", userId, isEligibleForBNPL);
    }

    // Update timestamp and re-evaluate BNPL eligibility before updating
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
        this.isEligibleForBNPL = evaluateEligibility();
        log.info("UserProfile updated: userId={}, eligibleForBNPL={}", userId, isEligibleForBNPL);
    }

    // BNPL eligibility based on annual income
    private Boolean evaluateEligibility() {
        return this.annualIncome != null && this.annualIncome >= 360000;
    }
}