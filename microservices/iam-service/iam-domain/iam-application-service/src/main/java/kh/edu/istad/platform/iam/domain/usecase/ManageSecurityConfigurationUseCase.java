package kh.edu.istad.platform.iam.domain.usecase;

import kh.edu.istad.platform.iam.domain.dto.SecurityConfigurationResult;
import kh.edu.istad.platform.iam.domain.port.out.AccountSecurityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ManageSecurityConfigurationUseCase {
    private final AccountSecurityRepository repository;
    public SecurityConfigurationResult get() { return repository.getSecurityConfiguration(); }
    public SecurityConfigurationResult update(SecurityConfigurationResult value) { return repository.updateSecurityConfiguration(value); }
}
