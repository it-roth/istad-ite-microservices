package kh.edu.istad.platform.customer.domain.dto;

import kh.edu.istad.platform.customer.domain.valueobject.CustomerStatus;

public record CustomerStatistics(
        CustomerStatus status,
        String segmentId,
        long total
) {
}
