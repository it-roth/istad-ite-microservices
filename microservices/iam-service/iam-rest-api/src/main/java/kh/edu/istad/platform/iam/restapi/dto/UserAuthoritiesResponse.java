package kh.edu.istad.platform.iam.restapi.dto;

import java.util.List;

public record UserAuthoritiesResponse(
        List<RoleWithExpiryResponse> roles,
        List<DirectPermissionResponse> directPermissions,
        List<String> effectivePermissions
) {
}


