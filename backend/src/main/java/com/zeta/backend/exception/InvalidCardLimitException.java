package com.zeta.backend.exception;

public class InvalidCardLimitException extends RuntimeException {
    public InvalidCardLimitException(String message) {
        super(message);
    }
}
