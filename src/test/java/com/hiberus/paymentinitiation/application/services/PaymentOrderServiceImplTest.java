package com.hiberus.paymentinitiation.application.services;

import com.hiberus.paymentinitiation.application.ports.output.PaymentOrderRepositoryPort;
import com.hiberus.paymentinitiation.domain.PaymentOrder;
import com.hiberus.paymentinitiation.infrastructure.adapters.api.dtos.InitiatePaymentOrderRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentOrderServiceImplTest {

    @Mock
    private PaymentOrderRepositoryPort repository;

    @InjectMocks
    private PaymentOrderServiceImpl service;

    @Captor
    private ArgumentCaptor<PaymentOrder> orderCaptor;

    @Test
    void shouldInitiatePaymentOrder() {
        // Given
        InitiatePaymentOrderRequest request = new InitiatePaymentOrderRequest();
        request.setExternalReference("EXT-1");
        var debtor = new InitiatePaymentOrderRequest.Account();
        debtor.setIban("DEBTOR-IBAN");
        request.setDebtorAccount(debtor);
        var creditor = new InitiatePaymentOrderRequest.Account();
        creditor.setIban("CREDITOR-IBAN");
        request.setCreditorAccount(creditor);
        var amount = new InitiatePaymentOrderRequest.Amount();
        amount.setAmount(BigDecimal.valueOf(100.0));
        amount.setCurrency("USD");
        request.setInstructedAmount(amount);
        request.setRemittanceInformation("Test");
        request.setRequestedExecutionDate(LocalDate.now());

        var savedOrder = PaymentOrder.builder()
                .id("PO-0001")
                .externalReference("EXT-1")
                .status(PaymentOrder.PaymentOrderStatus.PENDING)
                .build();
        when(repository.save(any())).thenReturn(savedOrder);

        // When
        var result = service.initiatePaymentOrder(request);

        // Then
        verify(repository).save(orderCaptor.capture());
        var capturedOrder = orderCaptor.getValue();
        assertThat(capturedOrder.getExternalReference()).isEqualTo("EXT-1");
        assertThat(capturedOrder.getDebtorAccount().getIban()).isEqualTo("DEBTOR-IBAN");
        assertThat(result.getId()).isEqualTo("PO-0001");
        assertThat(result.getStatus()).isEqualTo(PaymentOrder.PaymentOrderStatus.PENDING);
    }

    @Test
    void shouldRetrievePaymentOrder() {
        // Given
        var order = PaymentOrder.builder().id("PO-0001").build();
        when(repository.findById("PO-0001")).thenReturn(Optional.of(order));

        // When
        var result = service.retrievePaymentOrder("PO-0001");

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo("PO-0001");
    }
}
