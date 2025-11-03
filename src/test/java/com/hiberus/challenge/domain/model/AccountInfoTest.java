package com.hiberus.challenge.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("AccountInfo Value Object Tests")
class AccountInfoTest {

    @Test
    @DisplayName("Should validate correct IBAN format")
    void shouldValidateCorrectIbanFormat() {
        // Given
        AccountInfo accountInfo = AccountInfo.builder()
                .identification("ES9121000418450200051332")
                .name("John Doe")
                .build();

        // When & Then
        assertThatCode(accountInfo::validate).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Should fail with invalid IBAN format")
    void shouldFailWithInvalidIbanFormat() {
        // Given
        AccountInfo accountInfo = AccountInfo.builder()
                .identification("INVALID-IBAN")
                .name("John Doe")
                .build();

        // When & Then
        assertThatThrownBy(accountInfo::validate)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid IBAN format");
    }

    @Test
    @DisplayName("Should fail when identification is null")
    void shouldFailWhenIdentificationIsNull() {
        // Given
        AccountInfo accountInfo = AccountInfo.builder()
                .identification(null)
                .name("John Doe")
                .build();

        // When & Then
        assertThatThrownBy(accountInfo::validate)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Account identification is required");
    }

    @Test
    @DisplayName("Should fail when name is null")
    void shouldFailWhenNameIsNull() {
        // Given
        AccountInfo accountInfo = AccountInfo.builder()
                .identification("ES9121000418450200051332")
                .name(null)
                .build();

        // When & Then
        assertThatThrownBy(accountInfo::validate)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Account holder name is required");
    }

    @Test
    @DisplayName("Should fail when name exceeds 140 characters")
    void shouldFailWhenNameExceedsLimit() {
        // Given
        String longName = "A".repeat(141);
        AccountInfo accountInfo = AccountInfo.builder()
                .identification("ES9121000418450200051332")
                .name(longName)
                .build();

        // When & Then
        assertThatThrownBy(accountInfo::validate)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("must not exceed 140 characters");
    }

    @Test
    @DisplayName("Should validate correct BIC format")
    void shouldValidateCorrectBicFormat() {
        // Given
        AccountInfo accountInfo = AccountInfo.builder()
                .identification("ES9121000418450200051332")
                .name("John Doe")
                .bankIdentifier("BANKESMMXXX")
                .build();

        // When & Then
        assertThatCode(accountInfo::validate).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Should fail with invalid BIC format")
    void shouldFailWithInvalidBicFormat() {
        // Given
        AccountInfo accountInfo = AccountInfo.builder()
                .identification("ES9121000418450200051332")
                .name("John Doe")
                .bankIdentifier("INVALID")
                .build();

        // When & Then
        assertThatThrownBy(accountInfo::validate)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid BIC/SWIFT format");
    }
}
