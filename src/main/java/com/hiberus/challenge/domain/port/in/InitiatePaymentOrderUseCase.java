package com.hiberus.challenge.domain.port.in;

import com.hiberus.challenge.domain.model.PaymentOrder;
import reactor.core.publisher.Mono;

/**
 * Input port for initiating a new payment order.
 * This represents a use case in the application layer.
 */
public interface InitiatePaymentOrderUseCase {

    /**
     * Initiates a new payment order.
     *
     * @param command the command containing payment order details
     * @return the created payment order
     */
    Mono<PaymentOrder> initiatePaymentOrder(InitiatePaymentOrderCommand command);
}
