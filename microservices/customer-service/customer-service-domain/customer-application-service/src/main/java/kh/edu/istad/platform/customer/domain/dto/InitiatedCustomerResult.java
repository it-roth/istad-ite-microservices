package kh.edu.istad.platform.customer.domain.dto;

import java.util.UUID;

public record InitiatedCustomerResult(
        UUID customerId,
        String username,
        String familyName,
        String givenName,
        String email,
        String phoneNumber,
        String customerSegmentType
) {
}