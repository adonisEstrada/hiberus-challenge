package com.hiberus.paymentinitiation.infrastructure.adapters.api;

import com.hiberus.paymentinitiation.application.ports.input.PaymentOrderUseCase;
import com.hiberus.paymentinitiation.domain.PaymentOrder;
import com.hiberus.paymentinitiation.infrastructure.adapters.api.dtos.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class PaymentOrderControllerAdapter implements PaymentOrderApi {

    private final PaymentOrderUseCase useCase;

    @PostMapping("/payment-initiation/payment-orders")
    @Operation(summary = "Initiate a Payment Order")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Payment order initiated successfully"),
        @ApiResponse(responseCode = "400", description = "Bad request")
    })
    @Override
    public ResponseEntity<PaymentOrderResponse> initiatePaymentOrder(InitiatePaymentOrderRequest request) {
        PaymentOrder order = useCase.initiatePaymentOrder(request);
        PaymentOrderResponse response = toResponse(order);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/payment-initiation/payment-orders/{paymentOrderId}")
    @Operation(summary = "Retrieve Payment Order")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Payment order retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Payment order not found")
    })
    @Override
    public ResponseEntity<PaymentOrderResponse> retrievePaymentOrder(String paymentOrderId) {
        Optional<PaymentOrder> orderOpt = useCase.retrievePaymentOrder(paymentOrderId);
        return orderOpt.map(order -> ResponseEntity.ok(toResponse(order)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/payment-initiation/payment-orders/{paymentOrderId}/status")
    @Operation(summary = "Retrieve Payment Order Status")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Payment order status retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Payment order not found")
    })
    @Override
    public ResponseEntity<PaymentOrderStatusResponse> retrievePaymentOrderStatus(String paymentOrderId) {
        Optional<PaymentOrder> orderOpt = useCase.retrievePaymentOrderStatus(paymentOrderId);
        return orderOpt.map(order -> {
            PaymentOrderStatusResponse response = new PaymentOrderStatusResponse();
            response.setPaymentOrderId(order.getId());
            response.setStatus(order.getStatus().name());
            response.setLastUpdate(order.getLastUpdate());
            return ResponseEntity.ok(response);
        }).orElse(ResponseEntity.notFound().build());
    }

    private PaymentOrderResponse toResponse(PaymentOrder order) {
        PaymentOrderResponse response = new PaymentOrderResponse();
        response.setId(order.getId());
        response.setExternalReference(order.getExternalReference());

        PaymentOrderResponse.Account debtor = new PaymentOrderResponse.Account();
        debtor.setIban(order.getDebtorAccount().getIban());
        response.setDebtorAccount(debtor);

        PaymentOrderResponse.Account creditor = new PaymentOrderResponse.Account();
        creditor.setIban(order.getCreditorAccount().getIban());
        response.setCreditorAccount(creditor);

        PaymentOrderResponse.Amount amount = new PaymentOrderResponse.Amount();
        amount.setAmount(order.getInstructedAmount().getAmount());
        amount.setCurrency(order.getInstructedAmount().getCurrency());
        response.setInstructedAmount(amount);

        response.setRemittanceInformation(order.getRemittanceInformation());
        response.setRequestedExecutionDate(order.getRequestedExecutionDate());
        response.setStatus(order.getStatus().name());
        return response;
    }
}
