package kh.edu.istad.platform.iam;

import kh.edu.istad.platform.iam.domain.dto.OAuthClientResult;
import kh.edu.istad.platform.iam.domain.entity.User;
import kh.edu.istad.platform.iam.domain.exception.IamDomainException;
import kh.edu.istad.platform.iam.domain.port.out.OAuthClientRepository;
import kh.edu.istad.platform.iam.domain.service.IamDomainServiceImpl;
import kh.edu.istad.platform.iam.domain.usecase.GetOAuthConsentUseCase;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class IamUseCaseTest {

    @Test
    void directDenialRemovesPermissionGrantedByRoleOrDirectAssignment() {
        Set<String> effective = new IamDomainServiceImpl().calculateEffectivePermissions(
                Set.of("user:read", "report:read"),
                Set.of("report:write"),
                Set.of("report:read", "report:write")
        );

        assertEquals(Set.of("user:read"), effective);
    }

    @Test
    void userCannotUnlinkTheLastSignInMethodWithoutPassword() {
        IamDomainServiceImpl service = new IamDomainServiceImpl();

        assertThrows(
                IamDomainException.class,
                () -> service.validateIdentityCanBeUnlinked(User.builder().build(), 1)
        );
    }

    @Test
    void passwordAllowsUnlinkingTheLastFederatedIdentity() {
        User user = User.builder().passwordHash("hashed-password").build();

        assertDoesNotThrow(() -> new IamDomainServiceImpl().validateIdentityCanBeUnlinked(user, 1));
    }

    @Test
    void consentUseCaseSeparatesPreviouslyApprovedAndNewScopes() {
        OAuthClientRepository repository = new OAuthClientRepository() {
            @Override
            public Optional<OAuthClientResult> findByClientId(String clientId) {
                return Optional.of(new OAuthClientResult("registered-id", clientId, "Test client", Set.of()));
            }

            @Override
            public Set<String> findApprovedScopes(String registeredClientId, String principalName) {
                return Set.of("openid", "profile");
            }

            @Override
            public List<OAuthClientResult> findConnectedClients(String principalName) {
                return List.of();
            }

            @Override
            public void revokeConsent(String registeredClientId, String principalName) {
            }
        };
        GetOAuthConsentUseCase useCase = new GetOAuthConsentUseCase(repository);

        var result = useCase.execute("client-id", "user", Set.of("openid", "email"));

        assertEquals(Set.of("email"), result.requestedScopes());
        assertEquals(Set.of("openid"), result.previouslyApprovedScopes());
    }
}
