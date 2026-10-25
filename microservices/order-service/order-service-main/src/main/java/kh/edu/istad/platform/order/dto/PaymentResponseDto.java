package kh.edu.istad.platform.order.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record PaymentResponseDto(
        String paymentId,
        String orderId,
        String customerId,
        BigDecimal price,
        String status,
        String message,
        Instant processedAt
) {
}
