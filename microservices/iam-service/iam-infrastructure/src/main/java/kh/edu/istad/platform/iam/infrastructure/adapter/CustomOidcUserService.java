package kh.edu.istad.platform.iam.infrastructure.adapter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import kh.edu.istad.platform.iam.domain.dto.AuthUserResult;
import kh.edu.istad.platform.iam.domain.dto.FederatedIdentityCommand;
import kh.edu.istad.platform.iam.domain.port.out.AccountSecurityRepository;
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
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOidcUserService extends OidcUserService {

    private final FindOrProvisionFederatedUserUseCase findOrProvisionFederatedUserUseCase;
    private final LinkFederatedIdentityUseCase linkFederatedIdentityUseCase;
    private final SocialProfileExtractor socialProfileExtractor;
    private final AccountSecurityRepository accountSecurityRepository;

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
        String linkingUser = null;
        if (RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes reqAttr) {
            HttpServletRequest request = reqAttr.getRequest();
            HttpSession session = request.getSession(false);
            if (session != null) {
                linkingUser = (String) session.getAttribute("iam.link-user");
                session.removeAttribute("iam.link-user");
                session.setAttribute("iam.link-success", command.provider());
            }
        }
        try {
            if (linkingUser != null && !linkingUser.isBlank()) {
                AuthUserResult linked = linkFederatedIdentityUseCase.execute(linkingUser, command);
                try {
                    accountSecurityRepository.recordActivity(linked.uuid(), "IDENTITY_LINKED",
                            "Connected " + command.provider() + " single sign-on authentication",
                            linkingUser, "127.0.0.1");
                } catch (Exception ignored) {}
                return linked;
            }

            Authentication currentAuthentication = SecurityContextHolder.getContext().getAuthentication();
            if (currentAuthentication != null
                    && currentAuthentication.isAuthenticated()
                    && !(currentAuthentication instanceof AnonymousAuthenticationToken)) {
                AuthUserResult linked = linkFederatedIdentityUseCase.execute(currentAuthentication.getName(), command);
                try {
                    accountSecurityRepository.recordActivity(linked.uuid(), "IDENTITY_LINKED",
                            "Connected " + command.provider() + " single sign-on authentication",
                            currentAuthentication.getName(), "127.0.0.1");
                } catch (Exception ignored) {}
                return linked;
            }
            return findOrProvisionFederatedUserUseCase.execute(command);
        } catch (kh.edu.istad.platform.iam.domain.exception.IamDomainException ex) {
            log.warn("OAuth2/OIDC link error: {}", ex.getMessage());
            throw new OAuth2AuthenticationException(new org.springframework.security.oauth2.core.OAuth2Error(
                    "link_failed", ex.getMessage(), null
            ), ex);
        }
    }
}
