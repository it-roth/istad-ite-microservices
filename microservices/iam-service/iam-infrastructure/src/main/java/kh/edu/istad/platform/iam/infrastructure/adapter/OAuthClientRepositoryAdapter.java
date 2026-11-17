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
public class OAuthClientRepositoryAdapter implements OAuthClientRepository, kh.edu.istad.platform.iam.domain.port.out.OAuthClientAdminRepository {

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

    @Override
    public List<kh.edu.istad.platform.iam.domain.dto.OAuthClientAdminResult> findAll() {
        return jdbcTemplate.query("SELECT id FROM oauth2_registered_client ORDER BY client_name",
                (rs, n) -> registeredClientRepository.findById(rs.getString("id")))
                .stream().filter(java.util.Objects::nonNull).map(this::toAdminResult).toList();
    }

    @Override
    public Optional<kh.edu.istad.platform.iam.domain.dto.OAuthClientAdminResult> findById(String id) {
        return Optional.ofNullable(registeredClientRepository.findById(id)).map(this::toAdminResult);
    }

    @Override
    @Transactional
    public kh.edu.istad.platform.iam.domain.dto.OAuthClientAdminResult create(
            kh.edu.istad.platform.iam.domain.dto.CreateOAuthClientCommand command) {
        boolean requireConsent = Boolean.TRUE.equals(command.requireAuthorizationConsent());
        long accessMinutes = (command.accessTokenTimeToLiveMinutes() != null && command.accessTokenTimeToLiveMinutes() > 0)
                ? command.accessTokenTimeToLiveMinutes() : 5;
        long refreshMinutes = (command.refreshTokenTimeToLiveMinutes() != null && command.refreshTokenTimeToLiveMinutes() > 0)
                ? command.refreshTokenTimeToLiveMinutes() : 60;

        RegisteredClient.Builder builder = RegisteredClient.withId(java.util.UUID.randomUUID().toString())
                .clientId(command.clientId()).clientName(command.clientName())
                .clientSettings(org.springframework.security.oauth2.server.authorization.settings.ClientSettings.builder()
                        .requireAuthorizationConsent(requireConsent).requireProofKey(true).build())
                .tokenSettings(org.springframework.security.oauth2.server.authorization.settings.TokenSettings.builder()
                        .accessTokenTimeToLive(java.time.Duration.ofMinutes(accessMinutes))
                        .refreshTokenTimeToLive(java.time.Duration.ofMinutes(refreshMinutes))
                        .build());
        (command.authenticationMethods() == null ? Set.of("client_secret_basic") : command.authenticationMethods())
                .forEach(m -> builder.clientAuthenticationMethod(new org.springframework.security.oauth2.core.ClientAuthenticationMethod(m)));
        (command.grantTypes() == null ? Set.of("authorization_code") : command.grantTypes())
                .forEach(g -> builder.authorizationGrantType(new org.springframework.security.oauth2.core.AuthorizationGrantType(g)));
        Set<String> scopes = command.scopes() == null ? Set.of() : command.scopes();
        Set<String> redirectUris = command.redirectUris() == null ? Set.of() : command.redirectUris();
        Set<String> postLogoutRedirectUris = command.postLogoutRedirectUris() == null ? Set.of() : command.postLogoutRedirectUris();
        scopes.forEach(builder::scope);
        redirectUris.forEach(builder::redirectUri);
        postLogoutRedirectUris.forEach(builder::postLogoutRedirectUri);
        if (command.clientSecret() != null && !command.clientSecret().isBlank()) {
            String secret = command.clientSecret();
            if (!secret.startsWith("$2a$") && !secret.startsWith("$2b$") && !secret.startsWith("{noop}")) {
                secret = new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder().encode(secret);
            }
            builder.clientSecret(secret);
        }
        RegisteredClient client = builder.build();
        registeredClientRepository.save(client);
        return toAdminResult(client);
    }

    @Override
    @Transactional
    public void delete(String id) {
        jdbcTemplate.update("DELETE FROM oauth2_authorization_consent WHERE registered_client_id = ?", id);
        jdbcTemplate.update("DELETE FROM oauth2_authorization WHERE registered_client_id = ?", id);
        jdbcTemplate.update("DELETE FROM oauth2_registered_client WHERE id = ?", id);
    }

    private kh.edu.istad.platform.iam.domain.dto.OAuthClientAdminResult toAdminResult(RegisteredClient c) {
        return new kh.edu.istad.platform.iam.domain.dto.OAuthClientAdminResult(c.getId(), c.getClientId(),
                c.getClientName(), c.getAuthorizationGrantTypes().stream().map(org.springframework.security.oauth2.core.AuthorizationGrantType::getValue).collect(java.util.stream.Collectors.toUnmodifiableSet()),
                c.getClientAuthenticationMethods().stream().map(org.springframework.security.oauth2.core.ClientAuthenticationMethod::getValue).collect(java.util.stream.Collectors.toUnmodifiableSet()),
                Set.copyOf(c.getScopes()), Set.copyOf(c.getRedirectUris()), c.getClientIdIssuedAt());
    }
}
