package com.hiberus.challenge.domain.model;

import lombok.Builder;
import lombok.Value;
import lombok.With;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

/**
 * Domain entity representing a Payment Order.
 * This is the core business entity following Domain-Driven Design principles.
 */
@Value
@Builder
@With
public class PaymentOrder {
    String paymentOrderId;
    AccountInfo debtorAccount;
    AccountInfo creditorAccount;
    MonetaryAmount amount;
    LocalDate executionDate;
    String remittanceInformation;
    String endToEndIdentification;
    PaymentPriority priority;
    PaymentOrderStatus status;
    String statusReason;
    OffsetDateTime createdAt;
    OffsetDateTime updatedAt;

    /**
     * Creates a new payment order with initial PENDING status.
     */
    public static PaymentOrder create(
            String paymentOrderId,
            AccountInfo debtorAccount,
            AccountInfo creditorAccount,
            MonetaryAmount amount,
            LocalDate executionDate,
            String remittanceInformation,
            String endToEndIdentification,
            PaymentPriority priority,
            OffsetDateTime now) {

        validateBusinessRules(debtorAccount, creditorAccount, amount, executionDate, now);

        return PaymentOrder.builder()
                .paymentOrderId(paymentOrderId)
                .debtorAccount(debtorAccount)
                .creditorAccount(creditorAccount)
                .amount(amount)
                .executionDate(executionDate)
                .remittanceInformation(remittanceInformation)
                .endToEndIdentification(endToEndIdentification)
                .priority(priority != null ? priority : PaymentPriority.NORMAL)
                .status(PaymentOrderStatus.PENDING)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    /**
     * Validates business rules for payment order creation.
     */
    private static void validateBusinessRules(
            AccountInfo debtorAccount,
            AccountInfo creditorAccount,
            MonetaryAmount amount,
            LocalDate executionDate,
            OffsetDateTime now) {

        if (debtorAccount.equals(creditorAccount)) {
            throw new IllegalArgumentException("Debtor and creditor accounts cannot be the same");
        }

        if (amount.getValue().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        if (executionDate.isBefore(now.toLocalDate())) {
            throw new IllegalArgumentException("Execution date cannot be in the past");
        }
    }

    /**
     * Updates the status of the payment order.
     */
    public PaymentOrder updateStatus(PaymentOrderStatus newStatus, String reason, OffsetDateTime now) {
        validateStatusTransition(this.status, newStatus);

        return this.withStatus(newStatus)
                .withStatusReason(reason)
                .withUpdatedAt(now);
    }

    /**
     * Validates allowed status transitions.
     */
    private void validateStatusTransition(PaymentOrderStatus currentStatus, PaymentOrderStatus newStatus) {
        boolean isValid = switch (currentStatus) {
            case PENDING -> newStatus == PaymentOrderStatus.PROCESSING
                    || newStatus == PaymentOrderStatus.REJECTED
                    || newStatus == PaymentOrderStatus.CANCELLED;
            case PROCESSING -> newStatus == PaymentOrderStatus.COMPLETED
                    || newStatus == PaymentOrderStatus.FAILED;
            case COMPLETED, FAILED, REJECTED, CANCELLED -> false;
        };

        if (!isValid) {
            throw new IllegalStateException(
                    String.format("Invalid status transition from %s to %s", currentStatus, newStatus));
        }
    }
}
