package kh.edu.istad.platform.iam.infrastructure;

import kh.edu.istad.platform.iam.domain.dto.OAuthClientResult;
import kh.edu.istad.platform.iam.infrastructure.adapter.OAuthClientRepositoryAdapter;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsent;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class OAuthClientRepositoryAdapterIntegrationTest {

    @Test
    void loadsConnectedClientAndRevokesItsPersistedAuthorization() {
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setURL("jdbc:h2:mem:" + UUID.randomUUID() + ";DB_CLOSE_DELAY=-1");
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.execute("""
                CREATE TABLE oauth2_authorization_consent (
                    registered_client_id VARCHAR(100) NOT NULL,
                    principal_name VARCHAR(200) NOT NULL
                )
                """);
        jdbcTemplate.execute("""
                CREATE TABLE oauth2_authorization (
                    id VARCHAR(100) PRIMARY KEY,
                    registered_client_id VARCHAR(100) NOT NULL,
                    principal_name VARCHAR(200) NOT NULL
                )
                """);
        jdbcTemplate.update(
                "INSERT INTO oauth2_authorization_consent (registered_client_id, principal_name) VALUES (?, ?)",
                "registered-id",
                "user"
        );
        jdbcTemplate.update(
                "INSERT INTO oauth2_authorization (id, registered_client_id, principal_name) VALUES (?, ?, ?)",
                "authorization-id",
                "registered-id",
                "user"
        );

        RegisteredClient client = mock(RegisteredClient.class);
        when(client.getId()).thenReturn("registered-id");
        when(client.getClientId()).thenReturn("public-client-id");
        when(client.getClientName()).thenReturn("Example client");
        OAuth2AuthorizationConsent consent = mock(OAuth2AuthorizationConsent.class);
        when(consent.getScopes()).thenReturn(Set.of("openid", "email"));
        RegisteredClientRepository registeredClientRepository = mock(RegisteredClientRepository.class);
        when(registeredClientRepository.findById("registered-id")).thenReturn(client);
        OAuth2AuthorizationConsentService consentService = mock(OAuth2AuthorizationConsentService.class);
        when(consentService.findById("registered-id", "user")).thenReturn(consent);
        OAuthClientRepositoryAdapter adapter = new OAuthClientRepositoryAdapter(
                registeredClientRepository,
                consentService,
                jdbcTemplate
        );

        List<OAuthClientResult> clients = adapter.findConnectedClients("user");
        adapter.revokeConsent("registered-id", "user");

        assertEquals(1, clients.size());
        assertEquals("public-client-id", clients.getFirst().clientId());
        assertEquals(Set.of("openid", "email"), clients.getFirst().scopes());
        verify(consentService).remove(consent);
        assertEquals(
                0,
                jdbcTemplate.queryForObject(
                        "SELECT COUNT(*) FROM oauth2_authorization WHERE registered_client_id = ? AND principal_name = ?",
                        Integer.class,
                        "registered-id",
                        "user"
                )
        );
    }
}
