package kh.edu.istad.platform.iam.domain.usecase;

import kh.edu.istad.platform.iam.domain.dto.*;
import kh.edu.istad.platform.iam.domain.port.out.OAuthClientAdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ManageOAuthClientsUseCase {
    private final OAuthClientAdminRepository repository;
    public List<OAuthClientAdminResult> list() { return repository.findAll(); }
    public OAuthClientAdminResult get(String id) { return repository.findById(id).orElseThrow(); }
    public OAuthClientAdminResult create(CreateOAuthClientCommand command) { return repository.create(command); }
    public void delete(String id) { repository.delete(id); }
}
