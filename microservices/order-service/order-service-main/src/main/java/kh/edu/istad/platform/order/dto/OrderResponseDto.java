package kh.edu.istad.platform.order.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record OrderResponseDto(
        String orderId,
        String trackingId,
        String customerId,
        String customerUsername,
        String businessId,
        String businessName,
        BigDecimal totalPrice,
        String orderStatus,
        String paymentStatus,
        String paymentId,
        String communicationModeUsed,
        String deliveryAddress,
        List<OrderItemRequestDto> items,
        Instant createdAt
) {
}
