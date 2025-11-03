package com.hiberus.challenge.infrastructure.adapter.out.persistence;

import com.hiberus.challenge.infrastructure.adapter.out.persistence.entity.PaymentOrderEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository interface.
 */
@Repository
public interface R2dbcPaymentOrderRepository extends R2dbcRepository<PaymentOrderEntity, String> {

    /**
     * Checks if a payment order with the given end-to-end identification exists.
     */
    Mono<Boolean> existsByEndToEndIdentification(String endToEndIdentification);
}
