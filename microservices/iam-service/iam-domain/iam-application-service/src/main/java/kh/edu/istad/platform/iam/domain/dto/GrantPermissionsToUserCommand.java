package kh.edu.istad.platform.iam.domain.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record GrantPermissionsToUserCommand(
        UUID userUuid,
        List<UUID> permissionUuids,
        String effect, // GRANT or DENY
        LocalDateTime expiresAt,
        String reason
) {}
