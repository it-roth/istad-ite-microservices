package kh.edu.istad.platform.iam.domain.usecase;

import kh.edu.istad.platform.iam.domain.entity.Role;
import kh.edu.istad.platform.iam.domain.exception.IamDomainException;
import kh.edu.istad.platform.iam.domain.port.out.CurrentActorProvider;
import kh.edu.istad.platform.iam.domain.port.out.RoleRepository;
import kh.edu.istad.platform.iam.domain.service.IamDomainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class ActivateRoleUseCase {

    private final RoleRepository roleRepository;
    private final CurrentActorProvider currentActorProvider;
    private final IamDomainService iamDomainService;

    public void execute(UUID roleUuid) {
        log.info("Activating role UUID: {}", roleUuid);
        Role role = roleRepository.findByUuid(roleUuid)
                .orElseThrow(() -> {
                    return new IamDomainException("Role not found: " + roleUuid);
                });

        iamDomainService.validateRoleCanBeModifiedOrDeleted(role);

        String actor = currentActorProvider.getCurrentActor();
        LocalDateTime now = LocalDateTime.now();

        Role updated = Role.builder()
                .id(role.getId())
                .internalId(role.getInternalId())
                .name(role.getName())
                .description(role.getDescription())
                .isSystem(role.getSystem())
                .isActive(true)
                .isDeleted(role.getDeleted())
                .deletedAt(role.getDeletedAt())
                .deletedBy(role.getDeletedBy())
                .createdAt(role.getCreatedAt())
                .createdBy(role.getCreatedBy())
                .updatedAt(now)
                .updatedBy(actor)
                .version(role.getVersion())
                .build();

        roleRepository.save(updated);
    }
}

