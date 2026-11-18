package kh.edu.istad.platform.iam.restapi.dto;

import jakarta.validation.constraints.Size;

public record UpdateRoleRequest(
        @Size(min = 2, max = 50, message = "Role name must be between 2 and 50 characters")
        String name,

        @Size(max = 255, message = "Description cannot exceed 255 characters")
        String description
) {
    public UpdateRoleRequest(String description) {
        this(null, description);
    }
}


