package kh.edu.istad.platform.iam.restapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;
import java.util.UUID;

public record GrantPermissionsToUserRequest(
        @NotEmpty(message = "At least one permission UUID is required")
        List<UUID> permissionUuids,

        @NotBlank(message = "Effect must be either GRANT or DENY")
        String effect,

        String reason
) {
}


