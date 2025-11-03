package com.hiberus.challenge.domain.model;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.util.Currency;

/**
 * Value Object representing a monetary amount.
 */
@Value
@Builder
public class MonetaryAmount {
    BigDecimal value;
    String currency;

    /**
     * Validates the monetary amount.
     */
    public void validate() {
        if (value == null) {
            throw new IllegalArgumentException("Amount value is required");
        }

        if (value.compareTo(new BigDecimal("0.01")) < 0) {
            throw new IllegalArgumentException("Amount must be at least 0.01");
        }

        if (currency == null || currency.trim().isEmpty()) {
            throw new IllegalArgumentException("Currency is required");
        }

        try {
            Currency.getInstance(currency);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid ISO 4217 currency code: " + currency);
        }
    }
}
