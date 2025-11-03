package com.hiberus.challenge.application.exception;

/**
 * Exception thrown when a payment order is not found.
 */
public class PaymentOrderNotFoundException extends RuntimeException {

    public PaymentOrderNotFoundException(String paymentOrderId) {
        super("Payment order not found: " + paymentOrderId);
    }
}
