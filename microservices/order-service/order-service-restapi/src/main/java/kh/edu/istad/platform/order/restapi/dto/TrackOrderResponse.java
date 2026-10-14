package kh.edu.istad.platform.order.restapi.dto;

import kh.edu.istad.common.domain.valueobject.OrderStatus;

import java.util.List;
import java.util.UUID;

public record TrackOrderResponse(
        UUID orderId,
        OrderStatus orderStatus,
        List<String> failureMessages
) {}
