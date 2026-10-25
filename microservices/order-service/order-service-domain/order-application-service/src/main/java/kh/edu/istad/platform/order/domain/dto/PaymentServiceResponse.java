package kh.edu.istad.platform.order.domain.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record PaymentServiceResponse(
        UUID paymentId,
        UUID orderId,
        UUID customerId,
        BigDecimal price,
        String status,
        String message,
        Instant processedAt
) {
}
