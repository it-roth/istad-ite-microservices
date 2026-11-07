package kh.edu.istad.platform.iam.domain.port.out;

import kh.edu.istad.platform.iam.domain.dto.PageResult;
import kh.edu.istad.platform.iam.domain.dto.RoleFilterQuery;
import kh.edu.istad.platform.iam.domain.dto.RoleSummary;
import kh.edu.istad.platform.iam.domain.entity.Role;
import kh.edu.istad.platform.iam.domain.entity.RolePermissionAssignment;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RoleRepository {
    Role save(Role role);
    Optional<Role> findById(Long id);
    Optional<Role> findByUuid(UUID uuid);
    Optional<Role> findByName(String name);
    List<Role> findAllByIds(Collection<Long> ids);
    List<Role> findAllByUuids(Collection<UUID> uuids);

    boolean existsByName(String name);
    PageResult<RoleSummary> findAll(RoleFilterQuery query);

    List<RolePermissionAssignment> findActiveRolePermissionAssignments(Long roleId);
    List<RolePermissionAssignment> findActiveRolePermissionAssignmentsByRoleIds(Collection<Long> roleIds);

    void savePermissionAssignments(List<RolePermissionAssignment> assignments);
}
