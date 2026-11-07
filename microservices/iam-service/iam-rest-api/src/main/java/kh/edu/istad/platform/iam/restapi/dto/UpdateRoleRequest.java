package kh.edu.istad.platform.iam.restapi.dto;

import jakarta.validation.constraints.Size;

public record UpdateRoleRequest(
        @Size(max = 255, message = "Description cannot exceed 255 characters")
        String description
) {
}


