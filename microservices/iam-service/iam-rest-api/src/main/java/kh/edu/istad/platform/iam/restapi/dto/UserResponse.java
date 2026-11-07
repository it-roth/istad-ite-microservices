package kh.edu.istad.platform.iam.restapi.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record UserResponse(
        UUID uuid,
        String username,
        String email,
        Boolean emailVerified,
        String name,
        String locale,
        Boolean enabled,
        List<UserRoleSummaryResponse> roles,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}


