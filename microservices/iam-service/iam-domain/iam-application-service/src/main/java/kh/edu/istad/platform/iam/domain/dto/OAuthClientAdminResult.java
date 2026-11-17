package kh.edu.istad.platform.iam.domain.dto;

import java.time.Instant;
import java.util.Set;

public record OAuthClientAdminResult(
        String id, String clientId, String clientName, Set<String> grantTypes,
        Set<String> authenticationMethods, Set<String> scopes, Set<String> redirectUris,
        Instant issuedAt
) {}
