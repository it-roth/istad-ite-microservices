package kh.edu.istad.platform.order.dto;

import java.math.BigDecimal;

public record OrderItemRequestDto(
        String productId,
        int quantity,
        BigDecimal price
) {
}
