package com.hiberus.paymentinitiation.infrastructure.adapters.persistence;

import com.hiberus.paymentinitiation.application.ports.output.PaymentOrderRepositoryPort;
import com.hiberus.paymentinitiation.domain.PaymentOrder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class InMemoryPaymentOrderRepositoryAdapter implements PaymentOrderRepositoryPort {

    private final Map<String, PaymentOrder> orders = new ConcurrentHashMap<>();

    @Override
    public PaymentOrder save(PaymentOrder order) {
        orders.put(order.getId(), order);
        return order;
    }

    @Override
    public Optional<PaymentOrder> findById(String id) {
        return Optional.ofNullable(orders.get(id));
    }
}
