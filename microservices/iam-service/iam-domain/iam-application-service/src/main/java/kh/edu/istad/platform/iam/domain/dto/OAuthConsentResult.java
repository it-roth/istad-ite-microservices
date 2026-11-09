package kh.edu.istad.platform.iam.domain.dto;

import java.util.Set;

public record OAuthConsentResult(
        String clientId,
        String clientName,
        Set<String> requestedScopes,
        Set<String> previouslyApprovedScopes
) {}
