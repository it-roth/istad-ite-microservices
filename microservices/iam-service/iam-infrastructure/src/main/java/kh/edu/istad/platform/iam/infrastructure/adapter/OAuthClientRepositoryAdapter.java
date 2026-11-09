package kh.edu.istad.platform.iam.infrastructure.adapter;

import kh.edu.istad.platform.iam.domain.dto.OAuthClientResult;
import kh.edu.istad.platform.iam.domain.port.out.OAuthClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsent;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class OAuthClientRepositoryAdapter implements OAuthClientRepository {

    private final RegisteredClientRepository registeredClientRepository;
    private final OAuth2AuthorizationConsentService authorizationConsentService;
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Optional<OAuthClientResult> findByClientId(String clientId) {
        RegisteredClient client = registeredClientRepository.findByClientId(clientId);
        return Optional.ofNullable(client).map(registeredClient -> toResult(registeredClient, Set.of()));
    }

    @Override
    public Set<String> findApprovedScopes(String registeredClientId, String principalName) {
        OAuth2AuthorizationConsent consent = authorizationConsentService.findById(
                registeredClientId,
                principalName
        );
        return consent == null ? Set.of() : Set.copyOf(consent.getScopes());
    }

    @Override
    public List<OAuthClientResult> findConnectedClients(String principalName) {
        List<String> registeredClientIds = jdbcTemplate.query(
                "SELECT registered_client_id FROM oauth2_authorization_consent WHERE principal_name = ?",
                (resultSet, rowNumber) -> resultSet.getString("registered_client_id"),
                principalName
        );
        List<OAuthClientResult> clients = new ArrayList<>();
        for (String registeredClientId : registeredClientIds) {
            RegisteredClient client = registeredClientRepository.findById(registeredClientId);
            OAuth2AuthorizationConsent consent = authorizationConsentService.findById(
                    registeredClientId,
                    principalName
            );
            if (client != null && consent != null) {
                clients.add(toResult(client, consent.getScopes()));
            }
        }
        return List.copyOf(clients);
    }

    @Override
    @Transactional
    public void revokeConsent(String registeredClientId, String principalName) {
        OAuth2AuthorizationConsent consent = authorizationConsentService.findById(
                registeredClientId,
                principalName
        );
        if (consent != null) {
            authorizationConsentService.remove(consent);
            jdbcTemplate.update(
                    "DELETE FROM oauth2_authorization WHERE registered_client_id = ? AND principal_name = ?",
                    registeredClientId,
                    principalName
            );
        }
    }

    private OAuthClientResult toResult(RegisteredClient client, Set<String> scopes) {
        return new OAuthClientResult(
                client.getId(),
                client.getClientId(),
                client.getClientName(),
                Set.copyOf(scopes)
        );
    }
}
