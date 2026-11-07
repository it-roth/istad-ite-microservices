package kh.edu.istad.platform.iam.domain.port.out;

import kh.edu.istad.platform.iam.domain.dto.PageResult;
import kh.edu.istad.platform.iam.domain.dto.UserFilterQuery;
import kh.edu.istad.platform.iam.domain.dto.UserSummary;
import kh.edu.istad.platform.iam.domain.entity.Identity;
import kh.edu.istad.platform.iam.domain.entity.User;
import kh.edu.istad.platform.iam.domain.entity.UserPermissionAssignment;
import kh.edu.istad.platform.iam.domain.entity.UserRoleAssignment;
import kh.edu.istad.platform.iam.domain.valueobject.SocialProvider;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    User save(User user);

    Optional<User> findById(Long id);
    Optional<User> findByUuid(UUID uuid);
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findByUsernameOrEmail(String usernameOrEmail);

    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    PageResult<UserSummary> findAll(UserFilterQuery query);

    List<UserRoleAssignment> findActiveRoleAssignments(Long userId);
    List<UserPermissionAssignment> findActivePermissionAssignments(Long userId);

    void saveRoleAssignments(List<UserRoleAssignment> assignments);
    void savePermissionAssignments(List<UserPermissionAssignment> assignments);

    Optional<Identity> findIdentityByProviderAndSubject(SocialProvider provider, String providerSubject);
    List<Identity> findIdentitiesByUserId(Long userId);
    Identity saveIdentity(Identity identity);
}
