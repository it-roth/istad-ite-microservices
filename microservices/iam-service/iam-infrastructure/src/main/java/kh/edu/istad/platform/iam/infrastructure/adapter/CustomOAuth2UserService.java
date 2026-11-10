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
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final FindOrProvisionFederatedUserUseCase findOrProvisionFederatedUserUseCase;
    private final LinkFederatedIdentityUseCase linkFederatedIdentityUseCase;
    private final SocialProfileExtractor socialProfileExtractor;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        log.info("Processing OAuth2 login for registrationId: {}", registrationId);

        FederatedIdentityCommand command = socialProfileExtractor.extract(
                registrationId,
                oAuth2User.getAttributes()
        );

        AuthUserResult authUser = resolveUser(command);

        return new IamOAuth2User(
                authUser,
                oAuth2User.getAttributes(),
                null,
                null
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
