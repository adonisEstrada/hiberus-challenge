package com.hiberus.challenge.domain.port.in;

import com.hiberus.challenge.domain.model.PaymentOrder;
import reactor.core.publisher.Mono;

/**
 * Input port for retrieving the status of a payment order.
 */
public interface RetrievePaymentOrderStatusUseCase {

    /**
     * Retrieves the status of a payment order.
     *
     * @param paymentOrderId the unique identifier of the payment order
     * @return the payment order with status information
     */
    Mono<PaymentOrder> retrievePaymentOrderStatus(String paymentOrderId);
}
