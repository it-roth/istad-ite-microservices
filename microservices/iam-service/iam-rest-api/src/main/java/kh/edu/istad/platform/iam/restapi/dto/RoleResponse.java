package kh.edu.istad.platform.iam.restapi.dto;

import java.util.UUID;

public record RoleResponse(
        UUID uuid,
        String name,
        String description,
        Boolean active,
        Boolean system
) {
}


