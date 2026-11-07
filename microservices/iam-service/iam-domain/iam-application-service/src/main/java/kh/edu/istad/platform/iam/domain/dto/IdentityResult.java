package kh.edu.istad.platform.iam.domain.dto;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

public record IdentityResult(
        UUID uuid,
        String provider,
        String providerSubject,
        String email,
        Boolean emailVerified,
        String displayName,
        String username,
        String pictureUrl,
        Map<String, Object> attributes,
        LocalDateTime linkedAt,
        LocalDateTime lastLoginAt
) {}
