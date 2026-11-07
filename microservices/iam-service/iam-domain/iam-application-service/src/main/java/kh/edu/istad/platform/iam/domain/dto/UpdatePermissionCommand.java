package kh.edu.istad.platform.iam.domain.dto;

import java.util.UUID;

public record UpdatePermissionCommand(
        UUID permissionUuid,
        String description
) {}

