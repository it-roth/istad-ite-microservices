package kh.edu.istad.platform.iam.domain.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record AssignPermissionsToRoleCommand(
        UUID roleUuid,
        List<UUID> permissionUuids,
        LocalDateTime expiresAt,
        String reason
) {}
