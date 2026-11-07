package kh.edu.istad.platform.iam.domain.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record RoleAssignmentResult(
        UUID roleUuid,
        String roleName,
        LocalDateTime assignedAt,
        String assignedBy,
        LocalDateTime expiresAt
) {}
