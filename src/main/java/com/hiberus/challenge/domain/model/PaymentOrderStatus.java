package com.hiberus.challenge.domain.model;

/**
 * Enumeration representing the status of a payment order.
 * Aligned with BIAN Payment Initiation Service Domain.
 */
public enum PaymentOrderStatus {
    /**
     * Order received, awaiting processing.
     */
    PENDING,

    /**
     * Order is being executed.
     */
    PROCESSING,

    /**
     * Order successfully completed.
     */
    COMPLETED,

    /**
     * Order execution failed.
     */
    FAILED,

    /**
     * Order rejected by system or bank.
     */
    REJECTED,

    /**
     * Order cancelled by user.
     */
    CANCELLED
}
