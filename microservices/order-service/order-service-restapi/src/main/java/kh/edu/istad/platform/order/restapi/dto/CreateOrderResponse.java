package kh.edu.istad.platform.order.restapi.dto;

import kh.edu.istad.common.domain.valueobject.OrderStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record CreateOrderResponse(
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
        List<OrderItemResponse> items,
        Instant createdAt
) {
    public record OrderItemResponse(
            UUID productId,
            Integer quantity,
            BigDecimal price,
            BigDecimal subTotal
    ) {
    }
}
