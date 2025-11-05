package com.hiberus.paymentinitiation.domain;

import lombok.Builder;
import lombok.Value;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Value
@Builder
public class PaymentOrder {
    String id;
    String externalReference;
    Account debtorAccount;
    Account creditorAccount;
    Amount instructedAmount;
    String remittanceInformation;
    LocalDate requestedExecutionDate;
    PaymentOrderStatus status;
    LocalDateTime createdAt;
    LocalDateTime lastUpdate;

    public enum PaymentOrderStatus {
        PENDING, EXECUTED, FAILED
    }
}
