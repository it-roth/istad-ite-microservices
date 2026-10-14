package kh.edu.istad.platform.order.domain.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record CreateOrderCommand(
        UUID customerId,
        UUID businessId,
        BigDecimal price,
        List<OrderItemCommand> items,
        OrderAddressCommand address
) {
    public record OrderItemCommand(
            UUID productId,
            Integer quantity,
            BigDecimal price,
            BigDecimal subTotal
    ) {}

    public record OrderAddressCommand(
            String street,
            String postalCode,
            String city
    ) {}
}
