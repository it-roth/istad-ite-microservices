package kh.edu.istad.platform.iam.domain.dto;

import java.util.Set;

public record CreateOAuthClientCommand(
        String clientId,
        String clientName,
        String clientSecret,
        Set<String> grantTypes,
        Set<String> authenticationMethods,
        Set<String> scopes,
        Set<String> redirectUris,
        Set<String> postLogoutRedirectUris,
        Boolean requireAuthorizationConsent,
        Integer accessTokenTimeToLiveMinutes,
        Integer refreshTokenTimeToLiveMinutes
) {}
