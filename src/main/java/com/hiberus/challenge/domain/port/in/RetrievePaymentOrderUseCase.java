package com.hiberus.challenge.domain.port.in;

import com.hiberus.challenge.domain.model.PaymentOrder;
import reactor.core.publisher.Mono;

/**
 * Input port for retrieving a payment order.
 */
public interface RetrievePaymentOrderUseCase {

    /**
     * Retrieves a payment order by its ID.
     *
     * @param paymentOrderId the unique identifier of the payment order
     * @return the payment order if found
     */
    Mono<PaymentOrder> retrievePaymentOrder(String paymentOrderId);
}
