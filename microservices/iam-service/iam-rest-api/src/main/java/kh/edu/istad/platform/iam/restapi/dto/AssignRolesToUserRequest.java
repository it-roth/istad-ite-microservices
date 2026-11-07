package kh.edu.istad.platform.iam.restapi.dto;

import jakarta.validation.constraints.NotEmpty;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record AssignRolesToUserRequest(
        @NotEmpty(message = "At least one role UUID is required")
        List<UUID> roleUuids,

        LocalDateTime expiresAt,

        String reason
) {
}


