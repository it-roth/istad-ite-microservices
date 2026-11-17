package kh.edu.istad.platform.iam.domain.usecase;

import kh.edu.istad.platform.iam.domain.port.out.AccountSecurityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RevokeAccountSessionUseCase {
    private final AccountSecurityRepository repository;
    public void execute(UUID userId, String sessionId) { repository.revokeSession(userId, sessionId); }
    public void execute(String sessionId) { repository.revokeSession(sessionId); }
    public void executeOthers(UUID userId, String currentSessionId) { repository.revokeOtherSessions(userId, currentSessionId); }
}
