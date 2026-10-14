package kh.edu.istad.platform.order.domain.dto;

import kh.edu.istad.common.domain.valueobject.OrderStatus;

import java.util.UUID;

public record CreateOrderResult(
        UUID orderId,
        UUID trackingId,
        OrderStatus orderStatus,
        String message
) {}
