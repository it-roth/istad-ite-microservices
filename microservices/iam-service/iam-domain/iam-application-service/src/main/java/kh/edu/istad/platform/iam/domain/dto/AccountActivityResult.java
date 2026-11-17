package kh.edu.istad.platform.iam.domain.dto;

import java.time.OffsetDateTime;

public record AccountActivityResult(
        long id,
        String eventType,
        String message,
        String ipAddress,
        String actor,
        OffsetDateTime occurredAt
) {}
