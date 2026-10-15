package kh.edu.istad.platform.business.domain.dto;

import java.math.BigDecimal;
import java.util.List;

public record CreateBusinessCommand(
        String businessName,
        String email,
        String phoneNumber,
        List<ProductCommand> products
) {
    public record ProductCommand(
            String name,
            BigDecimal price,
            int quantity,
            boolean available
    ) {
    }
}
