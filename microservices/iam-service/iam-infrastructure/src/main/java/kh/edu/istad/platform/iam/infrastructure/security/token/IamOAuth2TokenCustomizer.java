package kh.edu.istad.platform.iam.infrastructure.security.token;

import kh.edu.istad.platform.iam.domain.dto.UserAuthoritiesResult;
import kh.edu.istad.platform.iam.domain.entity.User;
import kh.edu.istad.platform.iam.domain.port.out.UserRepository;
import kh.edu.istad.platform.iam.domain.usecase.GetUserAuthoritiesUseCase;
import kh.edu.istad.platform.iam.infrastructure.security.userdetails.IamUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.core.oidc.endpoint.OidcParameterNames;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class IamOAuth2TokenCustomizer implements OAuth2TokenCustomizer<JwtEncodingContext> {

    private final UserRepository userRepository;
    private final GetUserAuthoritiesUseCase getUserAuthoritiesUseCase;

    @Override
    public void customize(JwtEncodingContext context) {
        Authentication principal = context.getPrincipal();
        if (principal == null) {
            return;
        }

        String principalName = principal.getName();
        Optional<User> userOptional = findUser(principal);
        if (userOptional.isEmpty()) {
            log.debug("Token customizer: principal {} is not a recognized IAM user (could be client credentials)", principalName);
            return;
        }

        User user = userOptional.get();
        UUID userUuid = user.getId().value();
        Set<String> authorizedScopes = context.getAuthorizedScopes() != null
                ? context.getAuthorizedScopes()
                : Collections.emptySet();

        // 1. Customize ACCESS_TOKEN
        if (OAuth2TokenType.ACCESS_TOKEN.equals(context.getTokenType())) {
            context.getClaims().subject(userUuid.toString());

            UserAuthoritiesResult authoritiesResult = getUserAuthoritiesUseCase.execute(userUuid);
            List<String> roles = new ArrayList<>();
            List<String> permissions = new ArrayList<>();

            for (String auth : authoritiesResult.effectiveAuthorities()) {
                if (auth.startsWith("ROLE_")) {
                    roles.add(auth.substring(5));
                } else {
                    permissions.add(auth);
                }
            }

            context.getClaims().claim("roles", roles);
            context.getClaims().claim("permissions", permissions);
            context.getClaims().claim("username", user.getUsername());
        }

        // 2. Customize ID_TOKEN (OpenID Connect claims mapping)
        if (OidcParameterNames.ID_TOKEN.equals(context.getTokenType().getValue())) {
            context.getClaims().subject(userUuid.toString());

            if (authorizedScopes.contains(OidcScopes.PROFILE)) {
                mapProfileClaims(context, user);
            }

            if (authorizedScopes.contains(OidcScopes.EMAIL)) {
                if (user.getEmail() != null) {
                    context.getClaims().claim("email", user.getEmail());
                }
                if (user.getEmailVerified() != null) {
                    context.getClaims().claim("email_verified", user.getEmailVerified());
                }
            }

            if (authorizedScopes.contains(OidcScopes.PHONE)) {
                if (user.getPhoneNumber() != null) {
                    context.getClaims().claim("phone_number", user.getPhoneNumber());
                }
                if (user.getPhoneNumberVerified() != null) {
                    context.getClaims().claim("phone_number_verified", user.getPhoneNumberVerified());
                }
            }

            if (authorizedScopes.contains(OidcScopes.ADDRESS)) {
                Map<String, Object> addressMap = buildAddressClaim(user);
                if (!addressMap.isEmpty()) {
                    context.getClaims().claim("address", addressMap);
                }
            }
        }
    }

    private Optional<User> findUser(Authentication principal) {
        if (principal.getPrincipal() instanceof IamUserDetails iamUser) {
            return userRepository.findByUuid(iamUser.getUserUuid());
        }

        String name = principal.getName();
        try {
            UUID uuid = UUID.fromString(name);
            Optional<User> byUuid = userRepository.findByUuid(uuid);
            if (byUuid.isPresent()) {
                return byUuid;
            }
        } catch (IllegalArgumentException ignored) {
            // Not a UUID, search by username or email
        }

        return userRepository.findByUsernameOrEmail(name);
    }

    private void mapProfileClaims(JwtEncodingContext context, User user) {
        String fullName = buildFullName(user.getGivenName(), user.getFamilyName());
        if (fullName != null) {
            context.getClaims().claim("name", fullName);
        }
        if (user.getGivenName() != null) {
            context.getClaims().claim("given_name", user.getGivenName());
        }
        if (user.getFamilyName() != null) {
            context.getClaims().claim("family_name", user.getFamilyName());
        }
        if (user.getMiddleName() != null) {
            context.getClaims().claim("middle_name", user.getMiddleName());
        }
        if (user.getNickname() != null) {
            context.getClaims().claim("nickname", user.getNickname());
        }
        if (user.getUsername() != null) {
            context.getClaims().claim("preferred_username", user.getUsername());
        }
        if (user.getProfileUrl() != null) {
            context.getClaims().claim("profile", user.getProfileUrl());
        }
        if (user.getPictureUrl() != null) {
            context.getClaims().claim("picture", user.getPictureUrl());
        }
        if (user.getWebsiteUrl() != null) {
            context.getClaims().claim("website", user.getWebsiteUrl());
        }
        if (user.getGender() != null) {
            context.getClaims().claim("gender", user.getGender());
        }
        if (user.getBirthdate() != null) {
            context.getClaims().claim("birthdate", user.getBirthdate().toString());
        }
        if (user.getZoneinfo() != null) {
            context.getClaims().claim("zoneinfo", user.getZoneinfo());
        }
        if (user.getLocale() != null) {
            context.getClaims().claim("locale", user.getLocale());
        }
        if (user.getUpdatedAt() != null) {
            context.getClaims().claim("updated_at", user.getUpdatedAt().toEpochSecond(ZoneOffset.UTC));
        }
    }

    private String buildFullName(String givenName, String familyName) {
        if (givenName == null && familyName == null) {
            return null;
        }
        if (givenName != null && familyName != null) {
            return givenName.trim() + " " + familyName.trim();
        }
        return givenName != null ? givenName.trim() : familyName.trim();
    }

    private Map<String, Object> buildAddressClaim(User user) {
        Map<String, Object> address = new LinkedHashMap<>();
        if (user.getAddressStreetAddress() != null) {
            address.put("street_address", user.getAddressStreetAddress());
        }
        if (user.getAddressLocality() != null) {
            address.put("locality", user.getAddressLocality());
        }
        if (user.getAddressRegion() != null) {
            address.put("region", user.getAddressRegion());
        }
        if (user.getAddressPostalCode() != null) {
            address.put("postal_code", user.getAddressPostalCode());
        }
        if (user.getAddressCountry() != null) {
            address.put("country", user.getAddressCountry());
        }
        return address;
    }
}
