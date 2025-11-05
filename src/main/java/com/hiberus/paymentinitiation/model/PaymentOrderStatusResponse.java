package com.hiberus.paymentinitiation.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PaymentOrderStatusResponse {
    private String paymentOrderId;
    private String status;
    private LocalDateTime lastUpdate;
}
