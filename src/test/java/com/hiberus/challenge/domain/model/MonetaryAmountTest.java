package com.hiberus.challenge.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("MonetaryAmount Value Object Tests")
class MonetaryAmountTest {

    @Test
    @DisplayName("Should validate correct monetary amount")
    void shouldValidateCorrectMonetaryAmount() {
        // Given
        MonetaryAmount amount = MonetaryAmount.builder()
                .value(new BigDecimal("1500.50"))
                .currency("EUR")
                .build();

        // When & Then
        assertThatCode(amount::validate).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Should fail when value is null")
    void shouldFailWhenValueIsNull() {
        // Given
        MonetaryAmount amount = MonetaryAmount.builder()
                .value(null)
                .currency("EUR")
                .build();

        // When & Then
        assertThatThrownBy(amount::validate)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Amount value is required");
    }

    @Test
    @DisplayName("Should fail when value is less than 0.01")
    void shouldFailWhenValueIsTooSmall() {
        // Given
        MonetaryAmount amount = MonetaryAmount.builder()
                .value(new BigDecimal("0.001"))
                .currency("EUR")
                .build();

        // When & Then
        assertThatThrownBy(amount::validate)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Amount must be at least 0.01");
    }

    @Test
    @DisplayName("Should fail when currency is null")
    void shouldFailWhenCurrencyIsNull() {
        // Given
        MonetaryAmount amount = MonetaryAmount.builder()
                .value(new BigDecimal("1500.50"))
                .currency(null)
                .build();

        // When & Then
        assertThatThrownBy(amount::validate)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Currency is required");
    }

    @Test
    @DisplayName("Should fail with invalid currency code")
    void shouldFailWithInvalidCurrencyCode() {
        // Given
        MonetaryAmount amount = MonetaryAmount.builder()
                .value(new BigDecimal("1500.50"))
                .currency("INVALID")
                .build();

        // When & Then
        assertThatThrownBy(amount::validate)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid ISO 4217 currency code");
    }

    @Test
    @DisplayName("Should validate common currency codes")
    void shouldValidateCommonCurrencyCodes() {
        // Given
        String[] currencies = {"EUR", "USD", "GBP", "JPY"};

        for (String currency : currencies) {
            MonetaryAmount amount = MonetaryAmount.builder()
                    .value(new BigDecimal("1500.50"))
                    .currency(currency)
                    .build();

            // When & Then
            assertThatCode(amount::validate)
                    .as("Currency " + currency + " should be valid")
                    .doesNotThrowAnyException();
        }
    }
}
