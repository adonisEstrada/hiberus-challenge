package com.hiberus.paymentinitiation.repository;

import com.hiberus.paymentinitiation.model.PaymentOrder;
import org.springframework.stereotype.Repository;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class PaymentOrderRepository {
    private final Map<String, PaymentOrder> orders = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public PaymentOrder save(PaymentOrder order) {
        if (order.getId() == null) {
            order.setId("PO-" + String.format("%04d", idGenerator.getAndIncrement()));
        }
        orders.put(order.getId(), order);
        return order;
    }

    public Optional<PaymentOrder> findById(String id) {
        return Optional.ofNullable(orders.get(id));
    }
}
