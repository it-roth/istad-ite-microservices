package kh.edu.istad.platform.iam.domain.port.out;

import kh.edu.istad.platform.iam.domain.dto.OAuthClientResult;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface OAuthClientRepository {
    Optional<OAuthClientResult> findByClientId(String clientId);

    Set<String> findApprovedScopes(String registeredClientId, String principalName);

    List<OAuthClientResult> findConnectedClients(String principalName);

    void revokeConsent(String registeredClientId, String principalName);
}
