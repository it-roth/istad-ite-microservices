package kh.edu.istad.platform.customer.domain.dto;

import kh.edu.istad.platform.customer.domain.valueobject.CustomerStatus;

public record CustomerFilter(
        String username,
        String familyName,
        String givenName,
        String email,
        CustomerStatus status,
        String segmentId
) {
}