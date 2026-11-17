package kh.edu.istad.platform.iam.domain.dto;

import java.time.OffsetDateTime;

public record AdminAuditLogResult(
        long id,
        String eventType,
        String message,
        String actor,
        String ipAddress,
        String targetSubject,
        OffsetDateTime occurredAt
) {}
