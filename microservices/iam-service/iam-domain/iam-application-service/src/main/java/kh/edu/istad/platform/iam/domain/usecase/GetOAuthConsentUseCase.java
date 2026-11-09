package kh.edu.istad.platform.iam.domain.usecase;

import kh.edu.istad.platform.iam.domain.dto.OAuthClientResult;
import kh.edu.istad.platform.iam.domain.dto.OAuthConsentResult;
import kh.edu.istad.platform.iam.domain.exception.IamDomainException;
import kh.edu.istad.platform.iam.domain.port.out.OAuthClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class GetOAuthConsentUseCase {

    private final OAuthClientRepository oauthClientRepository;

    public OAuthConsentResult execute(String clientId, String principalName, Set<String> requestedScopes) {
        OAuthClientResult client = oauthClientRepository.findByClientId(clientId)
                .orElseThrow(() -> new IamDomainException("OAuth client not found"));
        Set<String> requested = requestedScopes == null ? Set.of() : Set.copyOf(requestedScopes);
        Set<String> approved = oauthClientRepository.findApprovedScopes(
                client.registeredClientId(),
                principalName
        );
        Set<String> previouslyApproved = new HashSet<>(requested);
        previouslyApproved.retainAll(approved);
        Set<String> toApprove = new HashSet<>(requested);
        toApprove.removeAll(approved);
        return new OAuthConsentResult(client.clientId(), client.clientName(), toApprove, previouslyApproved);
    }
}
