package kh.edu.istad.platform.payment.restapi.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record PaymentResponse(
        String paymentId,
        String orderId,
        String customerId,
        BigDecimal price,
        String status,
        String message,
        Instant processedAt
) {
}
