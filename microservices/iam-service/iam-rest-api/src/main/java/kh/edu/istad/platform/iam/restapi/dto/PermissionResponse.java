package kh.edu.istad.platform.iam.restapi.dto;

import java.util.UUID;

public record PermissionResponse(
        UUID uuid,
        String name,
        String resource,
        String action,
        String description,
        Boolean active,
        Boolean system
) {
}


