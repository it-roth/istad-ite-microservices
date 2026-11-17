package kh.edu.istad.platform.iam.domain.usecase;

import kh.edu.istad.platform.iam.domain.dto.AdminAuditLogResult;
import kh.edu.istad.platform.iam.domain.port.out.AccountSecurityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ListAllAuditLogsUseCase {
    private final AccountSecurityRepository repository;

    public List<AdminAuditLogResult> execute(int limit) {
        return repository.findAllActivity(limit);
    }
}
