package com.hiberus.challenge.infrastructure.adapter.out.persistence;

import com.hiberus.challenge.domain.model.PaymentOrder;
import com.hiberus.challenge.domain.port.out.PaymentOrderRepository;
import com.hiberus.challenge.infrastructure.adapter.out.persistence.mapper.PaymentOrderPersistenceMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * Adapter implementing the repository port using R2DBC.
 * This is an output adapter in the hexagonal architecture.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentOrderRepositoryAdapter implements PaymentOrderRepository {

    private final R2dbcPaymentOrderRepository r2dbcRepository;
    private final PaymentOrderPersistenceMapper mapper;

    @Override
    public Mono<PaymentOrder> save(PaymentOrder paymentOrder) {
        log.debug("Saving payment order: {}", paymentOrder.getPaymentOrderId());

        return Mono.just(paymentOrder)
                .map(mapper::toEntity)
                .flatMap(r2dbcRepository::save)
                .map(mapper::toDomain)
                .doOnSuccess(saved -> log.debug("Payment order saved successfully: {}",
                        saved.getPaymentOrderId()));
    }

    @Override
    public Mono<PaymentOrder> findById(String paymentOrderId) {
        log.debug("Finding payment order by ID: {}", paymentOrderId);

        return r2dbcRepository.findById(paymentOrderId)
                .map(mapper::toDomain)
                .doOnSuccess(found -> log.debug("Payment order found: {}", paymentOrderId))
                .doOnError(error -> log.error("Error finding payment order: {}", paymentOrderId, error));
    }

    @Override
    public Mono<Boolean> existsByEndToEndIdentification(String endToEndIdentification) {
        log.debug("Checking existence by end-to-end identification: {}", endToEndIdentification);

        return r2dbcRepository.existsByEndToEndIdentification(endToEndIdentification);
    }
}
