package kh.edu.istad.platform.iam.domain.port.out;

import kh.edu.istad.platform.iam.domain.dto.*;
import java.util.List;
import java.util.Optional;

public interface OAuthClientAdminRepository {
    List<OAuthClientAdminResult> findAll();
    Optional<OAuthClientAdminResult> findById(String id);
    OAuthClientAdminResult create(CreateOAuthClientCommand command);
    OAuthClientAdminResult update(String id, CreateOAuthClientCommand command);
    void delete(String id);
}
