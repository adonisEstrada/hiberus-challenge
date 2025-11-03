package com.hiberus.challenge.application.service;

import com.hiberus.challenge.domain.model.PaymentOrder;
import com.hiberus.challenge.domain.port.in.InitiatePaymentOrderCommand;
import com.hiberus.challenge.domain.port.in.InitiatePaymentOrderUseCase;
import com.hiberus.challenge.domain.port.in.RetrievePaymentOrderStatusUseCase;
import com.hiberus.challenge.domain.port.in.RetrievePaymentOrderUseCase;
import com.hiberus.challenge.domain.port.out.PaymentOrderIdGenerator;
import com.hiberus.challenge.domain.port.out.PaymentOrderRepository;
import com.hiberus.challenge.application.exception.PaymentOrderNotFoundException;
import com.hiberus.challenge.application.exception.DuplicatePaymentOrderException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;

/**
 * Application service implementing payment order use cases.
 * This is the core of the application layer in hexagonal architecture.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentOrderService implements
        InitiatePaymentOrderUseCase,
        RetrievePaymentOrderUseCase,
        RetrievePaymentOrderStatusUseCase {

    private final PaymentOrderRepository repository;
    private final PaymentOrderIdGenerator idGenerator;

    @Override
    public Mono<PaymentOrder> initiatePaymentOrder(InitiatePaymentOrderCommand command) {
        log.info("Initiating payment order: debtorIban={}, creditorIban={}, amount={}",
                command.getDebtorAccount().getIdentification(),
                command.getCreditorAccount().getIdentification(),
                command.getAmount().getValue());

        // Validate input
        command.getDebtorAccount().validate();
        command.getCreditorAccount().validate();
        command.getAmount().validate();

        // Check for duplicate end-to-end identification
        return checkForDuplicates(command.getEndToEndIdentification())
                .then(Mono.defer(() -> {
                    // Generate ID and create domain entity
                    String paymentOrderId = idGenerator.generateId();
                    OffsetDateTime now = OffsetDateTime.now();

                    PaymentOrder paymentOrder = PaymentOrder.create(
                            paymentOrderId,
                            command.getDebtorAccount(),
                            command.getCreditorAccount(),
                            command.getAmount(),
                            command.getExecutionDate(),
                            command.getRemittanceInformation(),
                            command.getEndToEndIdentification(),
                            command.getPriority(),
                            now
                    );

                    // Save and return
                    return repository.save(paymentOrder)
                            .doOnSuccess(saved -> log.info("Payment order created successfully: id={}",
                                    saved.getPaymentOrderId()));
                }));
    }

    @Override
    public Mono<PaymentOrder> retrievePaymentOrder(String paymentOrderId) {
        log.info("Retrieving payment order: id={}", paymentOrderId);

        return repository.findById(paymentOrderId)
                .switchIfEmpty(Mono.error(new PaymentOrderNotFoundException(paymentOrderId)))
                .doOnSuccess(found -> log.info("Payment order found: id={}, status={}",
                        found.getPaymentOrderId(), found.getStatus()));
    }

    @Override
    public Mono<PaymentOrder> retrievePaymentOrderStatus(String paymentOrderId) {
        log.info("Retrieving payment order status: id={}", paymentOrderId);

        return repository.findById(paymentOrderId)
                .switchIfEmpty(Mono.error(new PaymentOrderNotFoundException(paymentOrderId)))
                .doOnSuccess(found -> log.info("Payment order status retrieved: id={}, status={}",
                        found.getPaymentOrderId(), found.getStatus()));
    }

    /**
     * Checks for duplicate end-to-end identification if provided.
     */
    private Mono<Void> checkForDuplicates(String endToEndIdentification) {
        if (endToEndIdentification == null || endToEndIdentification.trim().isEmpty()) {
            return Mono.empty();
        }

        return repository.existsByEndToEndIdentification(endToEndIdentification)
                .flatMap(exists -> exists
                        ? Mono.error(new DuplicatePaymentOrderException(
                        "Payment order with end-to-end identification " + endToEndIdentification + " already exists"))
                        : Mono.empty());
    }
}
