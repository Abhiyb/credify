package com.zeta.backend.exception;

public class CardApplicationNotFoundException extends RuntimeException {
    public CardApplicationNotFoundException(String message) {
        super(message);
    }
}
