package kh.edu.istad.platform.iam.domain.usecase;

import kh.edu.istad.platform.iam.domain.dto.PermissionResult;
import kh.edu.istad.platform.iam.domain.entity.Permission;
import kh.edu.istad.platform.iam.domain.entity.Role;
import kh.edu.istad.platform.iam.domain.entity.RolePermissionAssignment;
import kh.edu.istad.platform.iam.domain.exception.IamDomainException;
import kh.edu.istad.platform.iam.domain.mapper.PermissionResultMapper;
import kh.edu.istad.platform.iam.domain.port.out.PermissionRepository;
import kh.edu.istad.platform.iam.domain.port.out.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class GetRolePermissionsUseCase {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public List<PermissionResult> execute(UUID roleUuid) {
        log.info("Fetching permissions for role UUID: {}", roleUuid);
        Role role = roleRepository.findByUuid(roleUuid)
                .orElseThrow(() -> new IamDomainException("Role not found: " + roleUuid));

        List<RolePermissionAssignment> assignments = roleRepository.findActiveRolePermissionAssignments(role.getInternalId());
        if (assignments.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> permissionIds = assignments.stream()
                .map(RolePermissionAssignment::getPermissionId)
                .distinct()
                .toList();

        List<Permission> permissions = permissionRepository.findAllByIds(permissionIds);
        return permissions.stream()
                .filter(p -> Boolean.TRUE.equals(p.getActive()) && !Boolean.TRUE.equals(p.getDeleted()))
                .sorted(Comparator.comparing(Permission::getName, Comparator.nullsLast(String::compareTo)))
                .map(PermissionResultMapper::toResult)
                .toList();
    }
}
