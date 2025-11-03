package com.hiberus.challenge.application.service;

import com.hiberus.challenge.application.exception.DuplicatePaymentOrderException;
import com.hiberus.challenge.application.exception.PaymentOrderNotFoundException;
import com.hiberus.challenge.domain.model.AccountInfo;
import com.hiberus.challenge.domain.model.MonetaryAmount;
import com.hiberus.challenge.domain.model.PaymentOrder;
import com.hiberus.challenge.domain.model.PaymentOrderStatus;
import com.hiberus.challenge.domain.model.PaymentPriority;
import com.hiberus.challenge.domain.port.in.InitiatePaymentOrderCommand;
import com.hiberus.challenge.domain.port.out.PaymentOrderIdGenerator;
import com.hiberus.challenge.domain.port.out.PaymentOrderRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("PaymentOrderService Tests")
class PaymentOrderServiceTest {

    @Mock
    private PaymentOrderRepository repository;

    @Mock
    private PaymentOrderIdGenerator idGenerator;

    @InjectMocks
    private PaymentOrderService service;

    @Test
    @DisplayName("Should initiate payment order successfully")
    void shouldInitiatePaymentOrderSuccessfully() {
        // Given
        InitiatePaymentOrderCommand command = createTestCommand();
        when(idGenerator.generateId()).thenReturn("PO-2025110300001");
        when(repository.existsByEndToEndIdentification(anyString())).thenReturn(Mono.just(false));
        when(repository.save(any(PaymentOrder.class))).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        // When
        Mono<PaymentOrder> result = service.initiatePaymentOrder(command);

        // Then
        StepVerifier.create(result)
                .assertNext(order -> {
                    assertThat(order.getPaymentOrderId()).isEqualTo("PO-2025110300001");
                    assertThat(order.getStatus()).isEqualTo(PaymentOrderStatus.PENDING);
                    assertThat(order.getDebtorAccount()).isEqualTo(command.getDebtorAccount());
                    assertThat(order.getCreditorAccount()).isEqualTo(command.getCreditorAccount());
                })
                .verifyComplete();

        verify(repository).save(any(PaymentOrder.class));
    }

    @Test
    @DisplayName("Should fail when duplicate end-to-end identification")
    void shouldFailWhenDuplicateEndToEndIdentification() {
        // Given
        InitiatePaymentOrderCommand command = createTestCommand();
        when(repository.existsByEndToEndIdentification(anyString())).thenReturn(Mono.just(true));

        // When
        Mono<PaymentOrder> result = service.initiatePaymentOrder(command);

        // Then
        StepVerifier.create(result)
                .expectError(DuplicatePaymentOrderException.class)
                .verify();
    }

    @Test
    @DisplayName("Should retrieve payment order successfully")
    void shouldRetrievePaymentOrderSuccessfully() {
        // Given
        PaymentOrder existingOrder = createTestPaymentOrder();
        when(repository.findById("PO-2025110300001")).thenReturn(Mono.just(existingOrder));

        // When
        Mono<PaymentOrder> result = service.retrievePaymentOrder("PO-2025110300001");

        // Then
        StepVerifier.create(result)
                .assertNext(order -> assertThat(order.getPaymentOrderId()).isEqualTo("PO-2025110300001"))
                .verifyComplete();
    }

    @Test
    @DisplayName("Should fail when payment order not found")
    void shouldFailWhenPaymentOrderNotFound() {
        // Given
        when(repository.findById("NONEXISTENT")).thenReturn(Mono.empty());

        // When
        Mono<PaymentOrder> result = service.retrievePaymentOrder("NONEXISTENT");

        // Then
        StepVerifier.create(result)
                .expectError(PaymentOrderNotFoundException.class)
                .verify();
    }

    @Test
    @DisplayName("Should retrieve payment order status successfully")
    void shouldRetrievePaymentOrderStatusSuccessfully() {
        // Given
        PaymentOrder existingOrder = createTestPaymentOrder();
        when(repository.findById("PO-2025110300001")).thenReturn(Mono.just(existingOrder));

        // When
        Mono<PaymentOrder> result = service.retrievePaymentOrderStatus("PO-2025110300001");

        // Then
        StepVerifier.create(result)
                .assertNext(order -> {
                    assertThat(order.getPaymentOrderId()).isEqualTo("PO-2025110300001");
                    assertThat(order.getStatus()).isEqualTo(PaymentOrderStatus.PENDING);
                })
                .verifyComplete();
    }

    private InitiatePaymentOrderCommand createTestCommand() {
        AccountInfo debtor = AccountInfo.builder()
                .identification("ES9121000418450200051332")
                .name("John Doe")
                .build();

        AccountInfo creditor = AccountInfo.builder()
                .identification("ES7921000813610123456789")
                .name("Jane Smith")
                .build();

        MonetaryAmount amount = MonetaryAmount.builder()
                .value(new BigDecimal("1500.50"))
                .currency("EUR")
                .build();

        return InitiatePaymentOrderCommand.builder()
                .debtorAccount(debtor)
                .creditorAccount(creditor)
                .amount(amount)
                .executionDate(LocalDate.now().plusDays(1))
                .remittanceInformation("Test payment")
                .endToEndIdentification("E2E-001")
                .priority(PaymentPriority.NORMAL)
                .build();
    }

    private PaymentOrder createTestPaymentOrder() {
        AccountInfo debtor = AccountInfo.builder()
                .identification("ES9121000418450200051332")
                .name("John Doe")
                .build();

        AccountInfo creditor = AccountInfo.builder()
                .identification("ES7921000813610123456789")
                .name("Jane Smith")
                .build();

        MonetaryAmount amount = MonetaryAmount.builder()
                .value(new BigDecimal("1500.50"))
                .currency("EUR")
                .build();

        return PaymentOrder.builder()
                .paymentOrderId("PO-2025110300001")
                .debtorAccount(debtor)
                .creditorAccount(creditor)
                .amount(amount)
                .executionDate(LocalDate.now().plusDays(1))
                .remittanceInformation("Test payment")
                .priority(PaymentPriority.NORMAL)
                .status(PaymentOrderStatus.PENDING)
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .build();
    }
}
