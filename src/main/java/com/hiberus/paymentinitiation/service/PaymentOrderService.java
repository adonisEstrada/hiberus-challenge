package com.hiberus.paymentinitiation.service;

import com.hiberus.paymentinitiation.model.*;
import com.hiberus.paymentinitiation.repository.PaymentOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaymentOrderService {
    private final PaymentOrderRepository repository;

    public PaymentOrder createPaymentOrder(PaymentOrderRequest request) {
        PaymentOrder order = new PaymentOrder();
        order.setExternalReference(request.getExternalReference());
        order.setDebtorIban(request.getDebtorAccount().getIban());
        order.setCreditorIban(request.getCreditorAccount().getIban());
        order.setAmount(request.getInstructedAmount().getAmount());
        order.setCurrency(request.getInstructedAmount().getCurrency());
        order.setRemittanceInformation(request.getRemittanceInformation());
        order.setRequestedExecutionDate(request.getRequestedExecutionDate());
        order.setStatus("PENDING");
        order.setCreatedAt(LocalDateTime.now());
        order.setLastUpdate(LocalDateTime.now());
        return repository.save(order);
    }

    public Optional<PaymentOrderResponse> getPaymentOrder(String id) {
        return repository.findById(id).map(this::toResponse);
    }

    public Optional<PaymentOrderStatusResponse> getPaymentOrderStatus(String id) {
        return repository.findById(id).map(order -> {
            PaymentOrderStatusResponse response = new PaymentOrderStatusResponse();
            response.setPaymentOrderId(order.getId());
            response.setStatus(order.getStatus());
            response.setLastUpdate(order.getLastUpdate());
            return response;
        });
    }

    private PaymentOrderResponse toResponse(PaymentOrder order) {
        PaymentOrderResponse response = new PaymentOrderResponse();
        response.setId(order.getId());
        response.setExternalReference(order.getExternalReference());

        PaymentOrderResponse.Account debtor = new PaymentOrderResponse.Account();
        debtor.setIban(order.getDebtorIban());
        response.setDebtorAccount(debtor);

        PaymentOrderResponse.Account creditor = new PaymentOrderResponse.Account();
        creditor.setIban(order.getCreditorIban());
        response.setCreditorAccount(creditor);

        PaymentOrderResponse.Amount amount = new PaymentOrderResponse.Amount();
        amount.setAmount(order.getAmount());
        amount.setCurrency(order.getCurrency());
        response.setInstructedAmount(amount);

        response.setRemittanceInformation(order.getRemittanceInformation());
        response.setRequestedExecutionDate(order.getRequestedExecutionDate());
        response.setStatus(order.getStatus());
        return response;
    }
}
