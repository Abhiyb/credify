package com.zeta.backend.exception;

public class DuplicateCardApplicationException extends RuntimeException {
    public DuplicateCardApplicationException(String message) {
        super(message);
    }
}
