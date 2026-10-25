package kh.edu.istad.platform.order.dto;

import java.math.BigDecimal;

public record PaymentRequestDto(
        String orderId,
        String customerId,
        BigDecimal price,
        String paymentMethod
) {
}
