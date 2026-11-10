package kh.edu.istad.platform.iam.infrastructure.adapter;

import kh.edu.istad.platform.iam.domain.dto.AuthUserResult;
import kh.edu.istad.platform.iam.domain.dto.FederatedIdentityCommand;
import kh.edu.istad.platform.iam.domain.usecase.FindOrProvisionFederatedUserUseCase;
import kh.edu.istad.platform.iam.domain.usecase.LinkFederatedIdentityUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOidcUserService extends OidcUserService {

    private final FindOrProvisionFederatedUserUseCase findOrProvisionFederatedUserUseCase;
    private final LinkFederatedIdentityUseCase linkFederatedIdentityUseCase;
    private final SocialProfileExtractor socialProfileExtractor;

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        OidcUser oidcUser = super.loadUser(userRequest);
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        log.info("Processing OIDC login for registrationId: {}", registrationId);

        FederatedIdentityCommand command = socialProfileExtractor.extract(
                registrationId,
                oidcUser.getAttributes()
        );

        AuthUserResult authUser = resolveUser(command);

        return new IamOAuth2User(
                authUser,
                oidcUser.getAttributes(),
                oidcUser.getIdToken(),
                oidcUser.getUserInfo()
        );
    }

    private AuthUserResult resolveUser(FederatedIdentityCommand command) {
        Authentication currentAuthentication = SecurityContextHolder.getContext().getAuthentication();
        if (currentAuthentication != null
                && currentAuthentication.isAuthenticated()
                && !(currentAuthentication instanceof AnonymousAuthenticationToken)) {
            return linkFederatedIdentityUseCase.execute(currentAuthentication.getName(), command);
        }
        return findOrProvisionFederatedUserUseCase.execute(command);
    }
}
