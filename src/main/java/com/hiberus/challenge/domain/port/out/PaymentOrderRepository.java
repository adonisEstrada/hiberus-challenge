package com.hiberus.challenge.domain.port.out;

import com.hiberus.challenge.domain.model.PaymentOrder;
import reactor.core.publisher.Mono;

/**
 * Output port for payment order persistence.
 * This interface defines the contract for the repository without implementation details.
 */
public interface PaymentOrderRepository {

    /**
     * Saves a payment order.
     *
     * @param paymentOrder the payment order to save
     * @return the saved payment order
     */
    Mono<PaymentOrder> save(PaymentOrder paymentOrder);

    /**
     * Finds a payment order by its ID.
     *
     * @param paymentOrderId the unique identifier
     * @return the payment order if found, empty otherwise
     */
    Mono<PaymentOrder> findById(String paymentOrderId);

    /**
     * Checks if a payment order with the given end-to-end ID exists.
     *
     * @param endToEndIdentification the end-to-end identifier
     * @return true if exists, false otherwise
     */
    Mono<Boolean> existsByEndToEndIdentification(String endToEndIdentification);
}
