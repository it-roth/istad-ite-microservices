package kh.edu.istad.platform.iam.domain.dto;

import kh.edu.istad.platform.iam.domain.valueobject.SocialProvider;

import java.util.Map;

public record FederatedIdentityCommand(
        SocialProvider provider,
        String providerSubject,
        String email,
        Boolean emailVerified,
        String displayName,
        String username,
        String pictureUrl,
        Map<String, Object> attributes
) {}
