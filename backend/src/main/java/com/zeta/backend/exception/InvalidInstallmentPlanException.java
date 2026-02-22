package com.zeta.backend.exception;

import com.zeta.backend.enums.InstallmentPlan;

/**
 * Exception thrown when an invalid installment plan is selected (e.g., 12-month plan).
 */
public class InvalidInstallmentPlanException extends BadRequestException {
    public InvalidInstallmentPlanException(InstallmentPlan plan) {
        super("Invalid installment plan: " + plan + ". Only THREE, SIX, or NINE months are supported.");
    }
}