package kh.edu.istad.platform.iam.domain.dto;

import java.util.UUID;

public record RoleSummary(
        UUID uuid,
        String name,
        Boolean isSystem,
        Boolean isActive
) {}

