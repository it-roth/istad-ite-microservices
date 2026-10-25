package kh.edu.istad.platform.order.domain.dto;

import java.util.List;
import java.util.UUID;

public record BusinessServiceResponse(
        BusinessId id,
        String businessName,
        boolean active,
        List<ProductResponse> products
) {
    public record BusinessId(UUID value) {
    }

    public record ProductResponse(
            ProductId id,
            String name,
            ProductPrice price,
            int quantity,
            boolean available
    ) {
        public record ProductId(UUID value) {
        }

        public record ProductPrice(java.math.BigDecimal amount) {
        }
    }
}
