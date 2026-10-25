package kh.edu.istad.platform.order.domain.dto;

import kh.edu.istad.common.domain.valueobject.OrderStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record CreateOrderResult(
        UUID orderId,
        UUID trackingId,
        UUID customerId,
        String customerUsername,
        UUID businessId,
        String businessName,
        BigDecimal totalPrice,
        OrderStatus orderStatus,
        String message,
        UUID paymentId,
        String paymentStatus,
        String communicationModeUsed,
        String deliveryAddress,
        List<OrderItemResult> items,
        Instant createdAt
) {
    public record OrderItemResult(
            UUID productId,
            Integer quantity,
            BigDecimal price,
            BigDecimal subTotal
    ) {
    }
}
