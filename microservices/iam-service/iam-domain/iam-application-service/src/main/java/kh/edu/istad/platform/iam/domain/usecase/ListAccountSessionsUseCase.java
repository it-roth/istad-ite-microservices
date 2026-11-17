package kh.edu.istad.platform.iam.domain.usecase;

import kh.edu.istad.platform.iam.domain.dto.AccountSessionResult;
import kh.edu.istad.platform.iam.domain.port.out.AccountSecurityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ListAccountSessionsUseCase {
    private final AccountSecurityRepository repository;
    public List<AccountSessionResult> execute(UUID userId, String currentSessionId) {
        return repository.findSessions(userId, currentSessionId);
    }
}
