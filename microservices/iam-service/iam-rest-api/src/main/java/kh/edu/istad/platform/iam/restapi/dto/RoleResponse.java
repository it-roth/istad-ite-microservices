package kh.edu.istad.platform.iam.restapi.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record RoleResponse(
        UUID uuid,
        String name,
        String description,
        Boolean active,
        Boolean system,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public RoleResponse(
            UUID uuid,
            String name,
            String description,
            Boolean active,
            Boolean system
    ) {
        this(uuid, name, description, active, system, null, null);
    }
}


