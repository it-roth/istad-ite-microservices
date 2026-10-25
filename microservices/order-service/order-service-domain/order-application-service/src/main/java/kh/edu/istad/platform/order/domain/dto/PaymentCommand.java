package kh.edu.istad.platform.order.domain.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record PaymentCommand(
        UUID orderId,
        UUID customerId,
        BigDecimal price,
        String paymentMethod
) {
}
