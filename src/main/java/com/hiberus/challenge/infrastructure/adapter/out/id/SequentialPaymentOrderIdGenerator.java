package com.hiberus.challenge.infrastructure.adapter.out.id;

import com.hiberus.challenge.domain.port.out.PaymentOrderIdGenerator;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Generates sequential payment order IDs in the format: PO-{timestamp}{sequence}.
 * Format: PO-YYYYMMDDHHMMSS{sequence}
 * Example: PO-2025110312345600001
 */
@Component
public class SequentialPaymentOrderIdGenerator implements PaymentOrderIdGenerator {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    private final AtomicLong sequence = new AtomicLong(0);

    @Override
    public String generateId() {
        String timestamp = LocalDateTime.now().format(FORMATTER);
        long seq = sequence.incrementAndGet() % 100000; // Keep sequence within 5 digits
        return String.format("PO-%s%05d", timestamp, seq);
    }
}
