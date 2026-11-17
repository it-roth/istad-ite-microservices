package kh.edu.istad.platform.iam.domain.dto;

import java.time.OffsetDateTime;

public record LoginHistoryResult(
        long id,
        boolean success,
        String method,
        String userAgent,
        String ipAddress,
        String location,
        OffsetDateTime occurredAt
) {}
