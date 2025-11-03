package com.hiberus.challenge.domain.port.out;

/**
 * Output port for generating payment order IDs.
 */
public interface PaymentOrderIdGenerator {

    /**
     * Generates a unique payment order ID.
     * Format: PO-{timestamp}{sequence}
     *
     * @return the generated ID
     */
    String generateId();
}
