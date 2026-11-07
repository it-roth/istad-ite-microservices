package kh.edu.istad.platform.iam.restapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreatePermissionRequest(
        @NotBlank(message = "Resource is required")
        @Size(max = 50, message = "Resource cannot exceed 50 characters")
        String resource,

        @NotBlank(message = "Action is required")
        @Size(max = 50, message = "Action cannot exceed 50 characters")
        String action,

        @Size(max = 255, message = "Description cannot exceed 255 characters")
        String description
) {
}


