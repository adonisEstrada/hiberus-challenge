package com.hiberus.challenge.domain.port.in;

import com.hiberus.challenge.domain.model.AccountInfo;
import com.hiberus.challenge.domain.model.MonetaryAmount;
import com.hiberus.challenge.domain.model.PaymentPriority;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;

/**
 * Command for initiating a payment order.
 * This is a DTO that crosses the application boundary.
 */
@Value
@Builder
public class InitiatePaymentOrderCommand {
    AccountInfo debtorAccount;
    AccountInfo creditorAccount;
    MonetaryAmount amount;
    LocalDate executionDate;
    String remittanceInformation;
    String endToEndIdentification;
    PaymentPriority priority;
}
