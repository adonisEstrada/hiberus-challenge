package com.hiberus.paymentinitiation.model;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class PaymentOrder {
    private String id;
    private String externalReference;
    private String debtorIban;
    private String creditorIban;
    private BigDecimal amount;
    private String currency;
    private String remittanceInformation;
    private LocalDate requestedExecutionDate;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime lastUpdate;
}
