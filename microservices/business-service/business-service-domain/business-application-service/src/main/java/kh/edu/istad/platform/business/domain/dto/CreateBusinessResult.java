package kh.edu.istad.platform.business.domain.dto;

import java.util.UUID;

public record CreateBusinessResult(
        UUID businessId,
        String businessName,
        String message
) {
}
