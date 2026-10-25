package kh.edu.istad.platform.order.dto;

import java.math.BigDecimal;
import java.util.List;

public record CreateOrderRequest(
        String customerId,
        String businessId,
        String deliveryStreet,
        String deliveryCity,
        String deliveryPostalCode,
        List<OrderItemRequestDto> items,
        String discoveryMode // "HARD_CODED" or "EUREKA"
) {
}
