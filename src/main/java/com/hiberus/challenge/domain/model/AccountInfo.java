package com.hiberus.challenge.domain.model;

import lombok.Builder;
import lombok.Value;

import java.util.regex.Pattern;

/**
 * Value Object representing account information.
 */
@Value
@Builder
public class AccountInfo {
    private static final Pattern IBAN_PATTERN = Pattern.compile("^[A-Z]{2}[0-9]{2}[A-Z0-9]+$");
    private static final Pattern BIC_PATTERN = Pattern.compile("^[A-Z]{6}[A-Z0-9]{2}([A-Z0-9]{3})?$");

    String identification;
    String name;
    String bankIdentifier;

    /**
     * Validates the account information.
     */
    public void validate() {
        if (identification == null || identification.trim().isEmpty()) {
            throw new IllegalArgumentException("Account identification is required");
        }

        if (!IBAN_PATTERN.matcher(identification).matches()) {
            throw new IllegalArgumentException("Invalid IBAN format");
        }

        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Account holder name is required");
        }

        if (name.length() > 140) {
            throw new IllegalArgumentException("Account holder name must not exceed 140 characters");
        }

        if (bankIdentifier != null && !BIC_PATTERN.matcher(bankIdentifier).matches()) {
            throw new IllegalArgumentException("Invalid BIC/SWIFT format");
        }
    }
}
