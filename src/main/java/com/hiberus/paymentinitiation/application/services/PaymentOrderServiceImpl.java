package com.hiberus.paymentinitiation.application.services;

import com.hiberus.paymentinitiation.application.ports.input.PaymentOrderUseCase;
import com.hiberus.paymentinitiation.application.ports.output.PaymentOrderRepositoryPort;
import com.hiberus.paymentinitiation.domain.PaymentOrder;
import com.hiberus.paymentinitiation.infrastructure.adapters.api.dtos.InitiatePaymentOrderRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Service
@RequiredArgsConstructor
public class PaymentOrderServiceImpl implements PaymentOrderUseCase {

    private final PaymentOrderRepositoryPort repository;
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public PaymentOrder initiatePaymentOrder(InitiatePaymentOrderRequest request) {
        PaymentOrder order = PaymentOrder.builder()
                .id("PO-" + String.format("%04d", idGenerator.getAndIncrement()))
                .externalReference(request.getExternalReference())
                .debtorAccount(new com.hiberus.paymentinitiation.domain.Account(request.getDebtorAccount().getIban()))
                .creditorAccount(new com.hiberus.paymentinitiation.domain.Account(request.getCreditorAccount().getIban()))
                .instructedAmount(new com.hiberus.paymentinitiation.domain.Amount(request.getInstructedAmount().getAmount(), request.getInstructedAmount().getCurrency()))
                .remittanceInformation(request.getRemittanceInformation())
                .requestedExecutionDate(request.getRequestedExecutionDate())
                .status(PaymentOrder.PaymentOrderStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .lastUpdate(LocalDateTime.now())
                .build();
        return repository.save(order);
    }

    @Override
    public Optional<PaymentOrder> retrievePaymentOrder(String id) {
        return repository.findById(id);
    }

    @Override
    public Optional<PaymentOrder> retrievePaymentOrderStatus(String id) {
        return repository.findById(id);
    }
}
