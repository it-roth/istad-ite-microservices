package kh.edu.istad.platform.iam.domain.dto;

import java.util.Set;

public record OAuthClientResult(
        String registeredClientId,
        String clientId,
        String clientName,
        Set<String> scopes
) {}
