package kh.edu.istad.platform.iam.domain.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public record UserAuthoritiesResult(
        UUID userUuid,
        List<RoleAuthorityItem> roles,
        List<DirectPermissionItem> directPermissions,
        Set<String> effectivePermissions,
        Set<String> effectiveAuthorities
) {

    public record RoleAuthorityItem(
            UUID uuid,
            String name,
            LocalDateTime expiresAt
    ) {
    }

    public record DirectPermissionItem(
            String name,
            String effect
    ) {
    }
}
