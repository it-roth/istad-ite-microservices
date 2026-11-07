package kh.edu.istad.platform.iam.domain.dto;

public record CreatePermissionCommand(
        String resource,
        String action,
        String description
) {}

