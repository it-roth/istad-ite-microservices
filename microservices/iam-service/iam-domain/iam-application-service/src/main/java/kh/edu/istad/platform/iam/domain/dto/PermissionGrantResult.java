package kh.edu.istad.platform.iam.domain.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record PermissionGrantResult(
        UUID permissionUuid,
        String permissionName,
        String effect,
        LocalDateTime assignedAt,
        String assignedBy,
        LocalDateTime expiresAt
) {}
