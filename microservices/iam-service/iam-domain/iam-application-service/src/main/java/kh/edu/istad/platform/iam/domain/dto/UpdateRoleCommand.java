package kh.edu.istad.platform.iam.domain.dto;

import java.util.UUID;

public record UpdateRoleCommand(
        UUID roleUuid,
        String name,
        String description
) {
    public UpdateRoleCommand(UUID roleUuid, String description) {
        this(roleUuid, null, description);
    }
}

