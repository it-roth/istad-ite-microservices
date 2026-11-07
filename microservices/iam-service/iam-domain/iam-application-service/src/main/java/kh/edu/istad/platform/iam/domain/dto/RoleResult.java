package kh.edu.istad.platform.iam.domain.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record RoleResult(
        UUID uuid,
        String name,
        String description,
        Boolean isSystem,
        Boolean isActive,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}
