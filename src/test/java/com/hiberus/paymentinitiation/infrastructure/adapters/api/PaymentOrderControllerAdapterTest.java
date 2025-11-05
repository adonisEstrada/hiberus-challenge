package com.hiberus.paymentinitiation.infrastructure.adapters.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hiberus.paymentinitiation.application.ports.input.PaymentOrderUseCase;
import com.hiberus.paymentinitiation.domain.PaymentOrder;
import com.hiberus.paymentinitiation.infrastructure.adapters.api.dtos.InitiatePaymentOrderRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(PaymentOrderControllerAdapter.class)
class PaymentOrderControllerAdapterTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PaymentOrderUseCase useCase;

    @Test
    void shouldInitiatePaymentOrder() throws Exception {
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

        var order = PaymentOrder.builder()
                .id("PO-0001")
                .externalReference("EXT-1")
                .debtorAccount(new com.hiberus.paymentinitiation.domain.Account("DEBTOR-IBAN"))
                .creditorAccount(new com.hiberus.paymentinitiation.domain.Account("CREDITOR-IBAN"))
                .instructedAmount(new com.hiberus.paymentinitiation.domain.Amount(BigDecimal.valueOf(100.0), "USD"))
                .remittanceInformation("Test")
                .requestedExecutionDate(LocalDate.now())
                .status(PaymentOrder.PaymentOrderStatus.PENDING)
                .build();
        when(useCase.initiatePaymentOrder(any())).thenReturn(order);
        when(useCase.retrievePaymentOrder("PO-0001")).thenReturn(Optional.of(order));

        // When & Then
        mockMvc.perform(post("/payment-initiation/payment-orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("PO-0001"));
    }
}
