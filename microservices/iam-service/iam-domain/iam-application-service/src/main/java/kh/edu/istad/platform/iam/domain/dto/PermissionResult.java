package kh.edu.istad.platform.iam.domain.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record PermissionResult(
        UUID uuid,
        String name,
        String resource,
        String action,
        String description,
        Boolean isSystem,
        Boolean isActive,
        LocalDateTime createdAt
) {}
