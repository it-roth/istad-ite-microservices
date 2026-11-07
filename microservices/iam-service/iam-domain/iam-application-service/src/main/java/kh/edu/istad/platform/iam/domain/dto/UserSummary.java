package kh.edu.istad.platform.iam.domain.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserSummary(
        UUID uuid,
        String username,
        String email,
        String givenName,
        String familyName,
        Boolean enabled,
        LocalDateTime createdAt
) {}
