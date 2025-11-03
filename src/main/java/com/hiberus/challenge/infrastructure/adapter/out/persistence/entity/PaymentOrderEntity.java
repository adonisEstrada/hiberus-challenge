package com.hiberus.challenge.infrastructure.adapter.out.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

/**
 * JPA Entity for payment orders.
 * This is the persistence model, separated from the domain model.
 */
@Table("payment_orders")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentOrderEntity {

    @Id
    @Column("payment_order_id")
    private String paymentOrderId;

    @Column("debtor_iban")
    private String debtorIban;

    @Column("debtor_name")
    private String debtorName;

    @Column("debtor_bic")
    private String debtorBic;

    @Column("creditor_iban")
    private String creditorIban;

    @Column("creditor_name")
    private String creditorName;

    @Column("creditor_bic")
    private String creditorBic;

    @Column("amount_value")
    private BigDecimal amountValue;

    @Column("amount_currency")
    private String amountCurrency;

    @Column("execution_date")
    private LocalDate executionDate;

    @Column("remittance_information")
    private String remittanceInformation;

    @Column("end_to_end_identification")
    private String endToEndIdentification;

    @Column("priority")
    private String priority;

    @Column("status")
    private String status;

    @Column("status_reason")
    private String statusReason;

    @Column("created_at")
    private OffsetDateTime createdAt;

    @Column("updated_at")
    private OffsetDateTime updatedAt;
}
