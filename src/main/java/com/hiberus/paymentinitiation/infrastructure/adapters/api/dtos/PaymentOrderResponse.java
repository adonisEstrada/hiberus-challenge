package com.hiberus.paymentinitiation.infrastructure.adapters.api.dtos;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class PaymentOrderResponse {
    private String id;
    private String externalReference;
    private Account debtorAccount;
    private Account creditorAccount;
    private Amount instructedAmount;
    private String remittanceInformation;
    private LocalDate requestedExecutionDate;
    private String status;

    @Data
    public static class Account {
        private String iban;
    }

    @Data
    public static class Amount {
        private BigDecimal amount;
        private String currency;
    }
}
