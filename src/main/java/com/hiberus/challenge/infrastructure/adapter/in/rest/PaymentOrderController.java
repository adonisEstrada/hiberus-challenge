package com.hiberus.challenge.infrastructure.adapter.in.rest;

import com.hiberus.challenge.domain.port.in.InitiatePaymentOrderUseCase;
import com.hiberus.challenge.domain.port.in.RetrievePaymentOrderStatusUseCase;
import com.hiberus.challenge.domain.port.in.RetrievePaymentOrderUseCase;
import com.hiberus.challenge.infrastructure.adapter.in.rest.api.PaymentOrdersApi;
import com.hiberus.challenge.infrastructure.adapter.in.rest.mapper.PaymentOrderRestMapper;
import com.hiberus.challenge.infrastructure.adapter.in.rest.model.InitiatePaymentOrderRequest;
import com.hiberus.challenge.infrastructure.adapter.in.rest.model.PaymentOrderResponse;
import com.hiberus.challenge.infrastructure.adapter.in.rest.model.PaymentOrderStatusResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;

/**
 * REST controller implementing the OpenAPI generated interface.
 * This is an input adapter in the hexagonal architecture.
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class PaymentOrderController implements PaymentOrdersApi {

    private final InitiatePaymentOrderUseCase initiatePaymentOrderUseCase;
    private final RetrievePaymentOrderUseCase retrievePaymentOrderUseCase;
    private final RetrievePaymentOrderStatusUseCase retrievePaymentOrderStatusUseCase;
    private final PaymentOrderRestMapper mapper;

    @Override
    public Mono<ResponseEntity<PaymentOrderResponse>> initiatePaymentOrder(
            Mono<InitiatePaymentOrderRequest> initiatePaymentOrderRequest,
            ServerWebExchange exchange) {

        log.info("POST /payment-initiation/payment-orders - Initiating payment order");

        return initiatePaymentOrderRequest
                .map(mapper::toDomainCommand)
                .flatMap(initiatePaymentOrderUseCase::initiatePaymentOrder)
                .map(mapper::toResponse)
                .map(response -> ResponseEntity
                        .created(URI.create("/payment-initiation/payment-orders/" + response.getPaymentOrderId()))
                        .body(response))
                .doOnSuccess(response -> log.info("Payment order created: {}",
                        response.getBody().getPaymentOrderId()));
    }

    @Override
    public Mono<ResponseEntity<PaymentOrderResponse>> retrievePaymentOrder(
            String paymentOrderId,
            ServerWebExchange exchange) {

        log.info("GET /payment-initiation/payment-orders/{} - Retrieving payment order", paymentOrderId);

        return retrievePaymentOrderUseCase.retrievePaymentOrder(paymentOrderId)
                .map(mapper::toResponse)
                .map(ResponseEntity::ok)
                .doOnSuccess(response -> log.info("Payment order retrieved: {}", paymentOrderId));
    }

    @Override
    public Mono<ResponseEntity<PaymentOrderStatusResponse>> retrievePaymentOrderStatus(
            String paymentOrderId,
            ServerWebExchange exchange) {

        log.info("GET /payment-initiation/payment-orders/{}/status - Retrieving payment order status",
                paymentOrderId);

        return retrievePaymentOrderStatusUseCase.retrievePaymentOrderStatus(paymentOrderId)
                .map(mapper::toStatusResponse)
                .map(ResponseEntity::ok)
                .doOnSuccess(response -> log.info("Payment order status retrieved: {}", paymentOrderId));
    }
}
