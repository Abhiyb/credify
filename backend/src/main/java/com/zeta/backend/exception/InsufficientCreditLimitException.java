package com.zeta.backend.exception;

/**
 * Exception thrown when a card's available credit limit is insufficient for a transaction.
 */
public class InsufficientCreditLimitException extends BadRequestException {
    public InsufficientCreditLimitException(double requestedAmount, double availableLimit) {
        super("Insufficient credit limit: requested " + requestedAmount + ", available " + availableLimit);
    }
}