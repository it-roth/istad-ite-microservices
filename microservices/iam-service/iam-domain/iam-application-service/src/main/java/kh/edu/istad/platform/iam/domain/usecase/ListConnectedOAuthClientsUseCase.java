package kh.edu.istad.platform.iam.domain.usecase;

import kh.edu.istad.platform.iam.domain.dto.OAuthClientResult;
import kh.edu.istad.platform.iam.domain.port.out.OAuthClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ListConnectedOAuthClientsUseCase {

    private final OAuthClientRepository oauthClientRepository;

    public List<OAuthClientResult> execute(String principalName) {
        return oauthClientRepository.findConnectedClients(principalName);
    }
}
