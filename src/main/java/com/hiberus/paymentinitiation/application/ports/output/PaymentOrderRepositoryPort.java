package com.hiberus.paymentinitiation.application.ports.output;

import com.hiberus.paymentinitiation.domain.PaymentOrder;
import java.util.Optional;

public interface PaymentOrderRepositoryPort {
    PaymentOrder save(PaymentOrder order);
    Optional<PaymentOrder> findById(String id);
}
