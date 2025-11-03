package com.hiberus.challenge.application.exception;

/**
 * Exception thrown when attempting to create a duplicate payment order.
 */
public class DuplicatePaymentOrderException extends RuntimeException {

    public DuplicatePaymentOrderException(String message) {
        super(message);
    }
}
