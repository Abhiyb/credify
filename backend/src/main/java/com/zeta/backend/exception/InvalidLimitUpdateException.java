package com.zeta.backend.exception;

public class InvalidLimitUpdateException extends RuntimeException {
    public InvalidLimitUpdateException(String message) {
        super(message);
    }
}
