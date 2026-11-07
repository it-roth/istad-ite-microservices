package kh.edu.istad.platform.iam.domain.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record PermissionAssignmentResult(
        UUID permissionUuid,
        String permissionName,
        LocalDateTime assignedAt,
        String assignedBy,
        LocalDateTime expiresAt
) {}
