package kh.edu.istad.platform.order.domain.dto;

import java.util.UUID;

public record CustomerServiceResponse(
        CustomerId id,
        String username,
        String familyName,
        String givenName,
        Object email,
        Object phoneNumber,
        String status
) {
    public record CustomerId(UUID value) {
    }
}
