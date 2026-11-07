package kh.edu.istad.platform.iam.domain.dto;

import java.util.Set;
import java.util.UUID;

public record UserAuthoritiesResult(
        UUID userUuid,
        Set<String> roles,
        Set<String> permissions,
        Set<String> effectiveAuthorities
) {}

