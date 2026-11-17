package kh.edu.istad.platform.iam.domain.usecase;

import kh.edu.istad.platform.iam.domain.dto.AccountSessionResult;
import kh.edu.istad.platform.iam.domain.port.out.AccountSecurityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ListAllSessionsUseCase {
    private final AccountSecurityRepository repository;
    public List<AccountSessionResult> execute() { return repository.findAllSessions(); }
}
