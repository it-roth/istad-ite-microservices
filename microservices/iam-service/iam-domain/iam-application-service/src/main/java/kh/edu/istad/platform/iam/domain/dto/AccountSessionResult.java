package kh.edu.istad.platform.iam.domain.dto;

import java.time.OffsetDateTime;

public record AccountSessionResult(
        String id,
        boolean current,
        String browser,
        String device,
        String ipAddress,
        String location,
        OffsetDateTime createdAt,
        OffsetDateTime lastActivity
) {}
