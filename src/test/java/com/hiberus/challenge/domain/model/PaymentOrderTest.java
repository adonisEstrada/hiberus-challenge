package com.hiberus.challenge.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("PaymentOrder Domain Model Tests")
class PaymentOrderTest {

    @Test
    @DisplayName("Should create payment order with valid data")
    void shouldCreatePaymentOrderWithValidData() {
        // Given
        AccountInfo debtor = createTestAccount("ES9121000418450200051332", "John Doe");
        AccountInfo creditor = createTestAccount("ES7921000813610123456789", "Jane Smith");
        MonetaryAmount amount = MonetaryAmount.builder().value(new BigDecimal("1500.50")).currency("EUR").build();
        LocalDate executionDate = LocalDate.now().plusDays(1);
        OffsetDateTime now = OffsetDateTime.now();

        // When
        PaymentOrder order = PaymentOrder.create(
                "PO-2025110300001",
                debtor,
                creditor,
                amount,
                executionDate,
                "Test payment",
                "E2E-001",
                PaymentPriority.NORMAL,
                now
        );

        // Then
        assertThat(order).isNotNull();
        assertThat(order.getPaymentOrderId()).isEqualTo("PO-2025110300001");
        assertThat(order.getStatus()).isEqualTo(PaymentOrderStatus.PENDING);
        assertThat(order.getPriority()).isEqualTo(PaymentPriority.NORMAL);
        assertThat(order.getCreatedAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("Should fail when debtor and creditor accounts are the same")
    void shouldFailWhenDebtorAndCreditorAreSame() {
        // Given
        AccountInfo account = createTestAccount("ES9121000418450200051332", "John Doe");
        MonetaryAmount amount = MonetaryAmount.builder().value(new BigDecimal("1500.50")).currency("EUR").build();
        LocalDate executionDate = LocalDate.now().plusDays(1);
        OffsetDateTime now = OffsetDateTime.now();

        // When & Then
        assertThatThrownBy(() -> PaymentOrder.create(
                "PO-2025110300001",
                account,
                account,
                amount,
                executionDate,
                "Test payment",
                "E2E-001",
                PaymentPriority.NORMAL,
                now
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Debtor and creditor accounts cannot be the same");
    }

    @Test
    @DisplayName("Should fail when amount is zero or negative")
    void shouldFailWhenAmountIsZeroOrNegative() {
        // Given
        AccountInfo debtor = createTestAccount("ES9121000418450200051332", "John Doe");
        AccountInfo creditor = createTestAccount("ES7921000813610123456789", "Jane Smith");
        MonetaryAmount amount = MonetaryAmount.builder().value(BigDecimal.ZERO).currency("EUR").build();
        LocalDate executionDate = LocalDate.now().plusDays(1);
        OffsetDateTime now = OffsetDateTime.now();

        // When & Then
        assertThatThrownBy(() -> PaymentOrder.create(
                "PO-2025110300001",
                debtor,
                creditor,
                amount,
                executionDate,
                "Test payment",
                "E2E-001",
                PaymentPriority.NORMAL,
                now
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Amount must be positive");
    }

    @Test
    @DisplayName("Should fail when execution date is in the past")
    void shouldFailWhenExecutionDateIsInPast() {
        // Given
        AccountInfo debtor = createTestAccount("ES9121000418450200051332", "John Doe");
        AccountInfo creditor = createTestAccount("ES7921000813610123456789", "Jane Smith");
        MonetaryAmount amount = MonetaryAmount.builder().value(new BigDecimal("1500.50")).currency("EUR").build();
        LocalDate executionDate = LocalDate.now().minusDays(1);
        OffsetDateTime now = OffsetDateTime.now();

        // When & Then
        assertThatThrownBy(() -> PaymentOrder.create(
                "PO-2025110300001",
                debtor,
                creditor,
                amount,
                executionDate,
                "Test payment",
                "E2E-001",
                PaymentPriority.NORMAL,
                now
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Execution date cannot be in the past");
    }

    @Test
    @DisplayName("Should update status from PENDING to PROCESSING")
    void shouldUpdateStatusFromPendingToProcessing() {
        // Given
        PaymentOrder order = createTestPaymentOrder(PaymentOrderStatus.PENDING);
        OffsetDateTime now = OffsetDateTime.now();

        // When
        PaymentOrder updated = order.updateStatus(PaymentOrderStatus.PROCESSING, "Processing payment", now);

        // Then
        assertThat(updated.getStatus()).isEqualTo(PaymentOrderStatus.PROCESSING);
        assertThat(updated.getStatusReason()).isEqualTo("Processing payment");
        assertThat(updated.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("Should fail when invalid status transition")
    void shouldFailWhenInvalidStatusTransition() {
        // Given
        PaymentOrder order = createTestPaymentOrder(PaymentOrderStatus.COMPLETED);
        OffsetDateTime now = OffsetDateTime.now();

        // When & Then
        assertThatThrownBy(() -> order.updateStatus(PaymentOrderStatus.PROCESSING, "Invalid", now))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Invalid status transition");
    }

    private AccountInfo createTestAccount(String iban, String name) {
        return AccountInfo.builder()
                .identification(iban)
                .name(name)
                .build();
    }

    private PaymentOrder createTestPaymentOrder(PaymentOrderStatus status) {
        AccountInfo debtor = createTestAccount("ES9121000418450200051332", "John Doe");
        AccountInfo creditor = createTestAccount("ES7921000813610123456789", "Jane Smith");
        MonetaryAmount amount = MonetaryAmount.builder().value(new BigDecimal("1500.50")).currency("EUR").build();

        return PaymentOrder.builder()
                .paymentOrderId("PO-2025110300001")
                .debtorAccount(debtor)
                .creditorAccount(creditor)
                .amount(amount)
                .executionDate(LocalDate.now().plusDays(1))
                .remittanceInformation("Test payment")
                .priority(PaymentPriority.NORMAL)
                .status(status)
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .build();
    }
}
