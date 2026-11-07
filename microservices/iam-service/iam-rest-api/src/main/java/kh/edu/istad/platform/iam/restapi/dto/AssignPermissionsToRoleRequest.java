package kh.edu.istad.platform.iam.restapi.dto;

import jakarta.validation.constraints.NotEmpty;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record AssignPermissionsToRoleRequest(
        @NotEmpty(message = "At least one permission UUID is required")
        List<UUID> permissionUuids,

        LocalDateTime expiresAt,

        String reason
) {
}


