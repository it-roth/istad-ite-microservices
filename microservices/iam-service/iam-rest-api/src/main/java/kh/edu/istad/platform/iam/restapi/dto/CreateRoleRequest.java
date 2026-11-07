package kh.edu.istad.platform.iam.restapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

public record CreateRoleRequest(
        @NotBlank(message = "Role name is required")
        @Size(max = 50, message = "Role name cannot exceed 50 characters")
        String name,

        String description,

        List<UUID> permissionUuids
) {
}


