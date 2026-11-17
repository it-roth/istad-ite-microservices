package kh.edu.istad.platform.iam.restapi.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.Set;

public record CreateOAuthClientRequest(
        @NotBlank String clientId,
        @NotBlank String clientName,
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
