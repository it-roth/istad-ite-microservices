package kh.edu.istad.platform.iam.domain.usecase;

import kh.edu.istad.platform.iam.domain.port.out.OAuthClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RevokeOAuthClientConsentUseCase {

    private final OAuthClientRepository oauthClientRepository;

    public void execute(String registeredClientId, String principalName) {
        oauthClientRepository.revokeConsent(registeredClientId, principalName);
    }
}
