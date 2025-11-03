package com.hiberus.challenge.infrastructure.adapter.in.rest;

import com.hiberus.challenge.infrastructure.adapter.in.rest.model.AccountInfo;
import com.hiberus.challenge.infrastructure.adapter.in.rest.model.InitiatePaymentOrderRequest;
import com.hiberus.challenge.infrastructure.adapter.in.rest.model.MonetaryAmount;
import com.hiberus.challenge.infrastructure.adapter.in.rest.model.PaymentOrderResponse;
import com.hiberus.challenge.infrastructure.adapter.in.rest.model.PaymentOrderStatusResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Testcontainers
@DisplayName("Payment Order Controller Integration Tests")
class PaymentOrderControllerIT {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("payment_db")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.r2dbc.url", () ->
                String.format("r2dbc:postgresql://%s:%d/%s",
                        postgres.getHost(),
                        postgres.getFirstMappedPort(),
                        postgres.getDatabaseName()));
        registry.add("spring.r2dbc.username", postgres::getUsername);
        registry.add("spring.r2dbc.password", postgres::getPassword);
    }

    @Autowired
    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        // Clean database before each test if needed
    }

    @Test
    @DisplayName("Should initiate payment order successfully")
    void shouldInitiatePaymentOrderSuccessfully() {
        // Given
        InitiatePaymentOrderRequest request = createTestRequest();

        // When & Then
        webTestClient.post()
                .uri("/payment-initiation/payment-orders")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().exists("Location")
                .expectBody(PaymentOrderResponse.class)
                .value(response -> {
                    assertThat(response.getPaymentOrderId()).isNotNull();
                    assertThat(response.getStatus()).isEqualTo(PaymentOrderResponse.StatusEnum.PENDING);
                    assertThat(response.getDebtorAccount().getIdentification())
                            .isEqualTo("ES9121000418450200051332");
                    assertThat(response.getAmount().getValue()).isEqualByComparingTo(new BigDecimal("1500.50"));
                });
    }

    @Test
    @DisplayName("Should retrieve payment order successfully")
    void shouldRetrievePaymentOrderSuccessfully() {
        // Given - Create a payment order first
        InitiatePaymentOrderRequest request = createTestRequest();
        PaymentOrderResponse created = webTestClient.post()
                .uri("/payment-initiation/payment-orders")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(PaymentOrderResponse.class)
                .returnResult()
                .getResponseBody();

        assertThat(created).isNotNull();
        String paymentOrderId = created.getPaymentOrderId();

        // When & Then - Retrieve the created order
        webTestClient.get()
                .uri("/payment-initiation/payment-orders/{id}", paymentOrderId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(PaymentOrderResponse.class)
                .value(response -> {
                    assertThat(response.getPaymentOrderId()).isEqualTo(paymentOrderId);
                    assertThat(response.getStatus()).isEqualTo(PaymentOrderResponse.StatusEnum.PENDING);
                });
    }

    @Test
    @DisplayName("Should retrieve payment order status successfully")
    void shouldRetrievePaymentOrderStatusSuccessfully() {
        // Given - Create a payment order first
        InitiatePaymentOrderRequest request = createTestRequest();
        PaymentOrderResponse created = webTestClient.post()
                .uri("/payment-initiation/payment-orders")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(PaymentOrderResponse.class)
                .returnResult()
                .getResponseBody();

        assertThat(created).isNotNull();
        String paymentOrderId = created.getPaymentOrderId();

        // When & Then - Retrieve the status
        webTestClient.get()
                .uri("/payment-initiation/payment-orders/{id}/status", paymentOrderId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(PaymentOrderStatusResponse.class)
                .value(response -> {
                    assertThat(response.getPaymentOrderId()).isEqualTo(paymentOrderId);
                    assertThat(response.getStatus()).isEqualTo(PaymentOrderStatusResponse.StatusEnum.PENDING);
                    assertThat(response.getUpdatedAt()).isNotNull();
                });
    }

    @Test
    @DisplayName("Should return 404 when payment order not found")
    void shouldReturn404WhenPaymentOrderNotFound() {
        // When & Then
        webTestClient.get()
                .uri("/payment-initiation/payment-orders/{id}", "PO-9999999999999")
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON);
    }

    @Test
    @DisplayName("Should return 400 when invalid request")
    void shouldReturn400WhenInvalidRequest() {
        // Given - Invalid request with same debtor and creditor
        InitiatePaymentOrderRequest request = new InitiatePaymentOrderRequest();
        AccountInfo sameAccount = new AccountInfo();
        sameAccount.setIdentification("ES9121000418450200051332");
        sameAccount.setName("Same Person");

        request.setDebtorAccount(sameAccount);
        request.setCreditorAccount(sameAccount);

        MonetaryAmount amount = new MonetaryAmount();
        amount.setValue(new BigDecimal("1500.50"));
        amount.setCurrency("EUR");
        request.setAmount(amount);

        request.setExecutionDate(LocalDate.now().plusDays(1));

        // When & Then
        webTestClient.post()
                .uri("/payment-initiation/payment-orders")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isBadRequest();
    }

    private InitiatePaymentOrderRequest createTestRequest() {
        InitiatePaymentOrderRequest request = new InitiatePaymentOrderRequest();

        AccountInfo debtor = new AccountInfo();
        debtor.setIdentification("ES9121000418450200051332");
        debtor.setName("John Doe");
        request.setDebtorAccount(debtor);

        AccountInfo creditor = new AccountInfo();
        creditor.setIdentification("ES7921000813610123456789");
        creditor.setName("Jane Smith");
        request.setCreditorAccount(creditor);

        MonetaryAmount amount = new MonetaryAmount();
        amount.setValue(new BigDecimal("1500.50"));
        amount.setCurrency("EUR");
        request.setAmount(amount);

        request.setExecutionDate(LocalDate.now().plusDays(1));
        request.setRemittanceInformation("Test payment");
        request.setEndToEndIdentification("E2E-TEST-" + System.currentTimeMillis());
        request.setPriority(InitiatePaymentOrderRequest.PriorityEnum.NORMAL);

        return request;
    }
}
