package kh.edu.istad.platform.iam.restapi.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record RoleWithExpiryResponse(
        UUID uuid,
        String name,
        LocalDateTime expiresAt
) {
}


