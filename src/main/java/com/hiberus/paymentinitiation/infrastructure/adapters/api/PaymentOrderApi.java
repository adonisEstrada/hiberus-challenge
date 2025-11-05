package com.hiberus.paymentinitiation.infrastructure.adapters.api;

import com.hiberus.paymentinitiation.infrastructure.adapters.api.dtos.InitiatePaymentOrderRequest;
import com.hiberus.paymentinitiation.infrastructure.adapters.api.dtos.PaymentOrderResponse;
import com.hiberus.paymentinitiation.infrastructure.adapters.api.dtos.PaymentOrderStatusResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

public interface PaymentOrderApi {

    ResponseEntity<PaymentOrderResponse> initiatePaymentOrder(@RequestBody InitiatePaymentOrderRequest request);

    ResponseEntity<PaymentOrderResponse> retrievePaymentOrder(@PathVariable String paymentOrderId);

    ResponseEntity<PaymentOrderStatusResponse> retrievePaymentOrderStatus(@PathVariable String paymentOrderId);
}
