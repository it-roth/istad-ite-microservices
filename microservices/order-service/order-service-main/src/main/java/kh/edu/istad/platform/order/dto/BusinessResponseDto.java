package kh.edu.istad.platform.order.dto;

import java.util.List;

public record BusinessResponseDto(
        Object id,
        String businessName,
        boolean active,
        List<Object> products
) {
}
