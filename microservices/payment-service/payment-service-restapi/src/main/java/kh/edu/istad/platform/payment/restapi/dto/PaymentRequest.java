package kh.edu.istad.platform.payment.restapi.dto;

import java.math.BigDecimal;

public record PaymentRequest(
        String orderId,
        String customerId,
        BigDecimal price,
        String paymentMethod
) {
}
