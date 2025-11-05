package com.hiberus.paymentinitiation.application.ports.input;

import com.hiberus.paymentinitiation.domain.PaymentOrder;
import com.hiberus.paymentinitiation.infrastructure.adapters.api.dtos.InitiatePaymentOrderRequest;
import java.util.Optional;

public interface PaymentOrderUseCase {
    PaymentOrder initiatePaymentOrder(InitiatePaymentOrderRequest request);
    Optional<PaymentOrder> retrievePaymentOrder(String id);
    Optional<PaymentOrder> retrievePaymentOrderStatus(String id);
}
