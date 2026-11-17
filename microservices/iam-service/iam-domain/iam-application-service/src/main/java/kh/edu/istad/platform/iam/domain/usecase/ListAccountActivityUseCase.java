package kh.edu.istad.platform.iam.domain.usecase;

import kh.edu.istad.platform.iam.domain.dto.AccountActivityResult;
import kh.edu.istad.platform.iam.domain.port.out.AccountSecurityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ListAccountActivityUseCase {
    private final AccountSecurityRepository repository;
    public List<AccountActivityResult> execute(UUID userId, int limit) { return repository.findActivity(userId, limit); }
}
