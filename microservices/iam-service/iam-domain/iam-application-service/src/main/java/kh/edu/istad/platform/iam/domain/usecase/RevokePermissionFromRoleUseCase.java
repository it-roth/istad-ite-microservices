package kh.edu.istad.platform.iam.domain.usecase;

import kh.edu.istad.platform.iam.domain.entity.Permission;
import kh.edu.istad.platform.iam.domain.entity.Role;
import kh.edu.istad.platform.iam.domain.entity.RolePermissionAssignment;
import kh.edu.istad.platform.iam.domain.exception.IamDomainException;
import kh.edu.istad.platform.iam.domain.port.out.CurrentActorProvider;
import kh.edu.istad.platform.iam.domain.port.out.PermissionRepository;
import kh.edu.istad.platform.iam.domain.port.out.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class RevokePermissionFromRoleUseCase {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final CurrentActorProvider currentActorProvider;

    public void execute(UUID roleUuid, UUID permissionUuid, String reason) {
        log.info("Revoking permission UUID: {} from role UUID: {}", permissionUuid, roleUuid);
        Role role = roleRepository.findByUuid(roleUuid)
                .orElseThrow(() -> {
                    return new IamDomainException("Role not found: " + roleUuid);
                });
        Permission permission = permissionRepository.findByUuid(permissionUuid)
                .orElseThrow(() -> {
                    return new IamDomainException("Permission not found: " + permissionUuid);
                });

        String actor = currentActorProvider.getCurrentActor();
        List<RolePermissionAssignment> assignments = roleRepository.findActiveRolePermissionAssignments(role.getInternalId());

        boolean updated = false;
        for (RolePermissionAssignment a : assignments) {
            if (permission.getInternalId().equals(a.getPermissionId())) {
                a.revoke(actor, reason);
                updated = true;
            }
        }

        if (updated) {
            roleRepository.savePermissionAssignments(assignments);
        }
    }
}

