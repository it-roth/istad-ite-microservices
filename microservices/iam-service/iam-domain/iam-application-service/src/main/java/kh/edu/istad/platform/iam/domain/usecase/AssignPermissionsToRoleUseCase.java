package kh.edu.istad.platform.iam.domain.usecase;

import kh.edu.istad.platform.iam.domain.dto.AssignPermissionsToRoleCommand;
import kh.edu.istad.platform.iam.domain.dto.PermissionAssignmentResult;
import kh.edu.istad.platform.iam.domain.entity.Permission;
import kh.edu.istad.platform.iam.domain.entity.Role;
import kh.edu.istad.platform.iam.domain.entity.RolePermissionAssignment;
import kh.edu.istad.platform.iam.domain.exception.IamDomainException;
import kh.edu.istad.platform.iam.domain.port.out.CurrentActorProvider;
import kh.edu.istad.platform.iam.domain.port.out.PermissionRepository;
import kh.edu.istad.platform.iam.domain.port.out.RoleRepository;
import kh.edu.istad.platform.iam.domain.service.IamDomainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class AssignPermissionsToRoleUseCase {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final CurrentActorProvider currentActorProvider;
    private final IamDomainService iamDomainService;

    public List<PermissionAssignmentResult> execute(AssignPermissionsToRoleCommand command) {
        log.info("Assigning permissions to role UUID: {}", command.roleUuid());
        Role role = roleRepository.findByUuid(command.roleUuid())
                .orElseThrow(() -> {
                    return new IamDomainException("Role not found: " + command.roleUuid());
                });

        iamDomainService.validateRoleCanBeAssigned(role);

        List<Permission> permissions = permissionRepository.findAllByUuids(command.permissionUuids());
        String actor = currentActorProvider.getCurrentActor();
        LocalDateTime now = LocalDateTime.now();

        List<RolePermissionAssignment> assignments = new ArrayList<>();
        List<PermissionAssignmentResult> results = new ArrayList<>();

        for (Permission permission : permissions) {
            iamDomainService.validatePermissionCanBeAssigned(permission);
            RolePermissionAssignment assignment = RolePermissionAssignment.builder()
                    .roleId(role.getInternalId())
                    .permissionId(permission.getInternalId())
                    .assignedAt(now)
                    .assignedBy(actor)
                    .expiresAt(command.expiresAt())
                    .reason(command.reason())
                    .build();
            assignments.add(assignment);
            results.add(new PermissionAssignmentResult(permission.getId().value(), permission.getName(), now, actor, command.expiresAt()));
        }

        roleRepository.savePermissionAssignments(assignments);
        return results;
    }
}

