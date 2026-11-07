package kh.edu.istad.platform.iam.domain.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record AssignRolesToUserCommand(
        UUID userUuid,
        List<UUID> roleUuids,
        LocalDateTime expiresAt,
        String reason
) {}
