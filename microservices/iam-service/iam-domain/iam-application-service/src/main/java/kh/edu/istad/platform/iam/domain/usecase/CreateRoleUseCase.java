package kh.edu.istad.platform.iam.domain.usecase;

import kh.edu.istad.platform.iam.domain.dto.CreateRoleCommand;
import kh.edu.istad.platform.iam.domain.dto.RoleResult;
import kh.edu.istad.platform.iam.domain.entity.Permission;
import kh.edu.istad.platform.iam.domain.entity.Role;
import kh.edu.istad.platform.iam.domain.entity.RolePermissionAssignment;
import kh.edu.istad.platform.iam.domain.exception.IamDomainException;
import kh.edu.istad.platform.iam.domain.mapper.RoleResultMapper;
import kh.edu.istad.platform.iam.domain.port.out.CurrentActorProvider;
import kh.edu.istad.platform.iam.domain.port.out.PermissionRepository;
import kh.edu.istad.platform.iam.domain.port.out.RoleRepository;
import kh.edu.istad.platform.iam.domain.service.IamDomainService;
import kh.edu.istad.platform.iam.domain.valueobject.RoleId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class CreateRoleUseCase {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final CurrentActorProvider currentActorProvider;
    private final IamDomainService iamDomainService;

    public RoleResult execute(CreateRoleCommand command) {
        log.info("Creating role with name: {}", command.name());
        String roleName = command.name().trim().toUpperCase();

        if (roleRepository.existsByName(roleName)) {
            throw new IamDomainException("Role '" + roleName + "' already exists");
        }

        String actor = currentActorProvider.getCurrentActor();
        LocalDateTime now = LocalDateTime.now();

        Role role = Role.builder()
                .id(new RoleId(UUID.randomUUID()))
                .name(roleName)
                .description(command.description())
                .isSystem(false)
                .isActive(true)
                .isDeleted(false)
                .createdAt(now)
                .createdBy(actor)
                .updatedAt(now)
                .updatedBy(actor)
                .version(0L)
                .build();

        Role savedRole = roleRepository.save(role);

        if (command.permissionUuids() != null && !command.permissionUuids().isEmpty()) {
            List<Permission> permissions = permissionRepository.findAllByUuids(command.permissionUuids());
            List<RolePermissionAssignment> assignments = new ArrayList<>();
            for (Permission p : permissions) {
                iamDomainService.validatePermissionCanBeAssigned(p);
                assignments.add(RolePermissionAssignment.builder()
                        .roleId(savedRole.getInternalId())
                        .permissionId(p.getInternalId())
                        .assignedAt(now)
                        .assignedBy(actor)
                        .reason("Assigned during role creation")
                        .build());
            }
            if (!assignments.isEmpty()) {
                roleRepository.savePermissionAssignments(assignments);
            }
        }

        return RoleResultMapper.toResult(savedRole);
    }
}

