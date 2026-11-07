package kh.edu.istad.platform.iam.domain.usecase;

import kh.edu.istad.platform.iam.domain.entity.Permission;
import kh.edu.istad.platform.iam.domain.exception.IamDomainException;
import kh.edu.istad.platform.iam.domain.port.out.CurrentActorProvider;
import kh.edu.istad.platform.iam.domain.port.out.PermissionRepository;
import kh.edu.istad.platform.iam.domain.service.IamDomainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class DeactivatePermissionUseCase {

    private final PermissionRepository permissionRepository;
    private final CurrentActorProvider currentActorProvider;
    private final IamDomainService iamDomainService;

    public void execute(UUID permissionUuid) {
        log.info("Deactivating permission UUID: {}", permissionUuid);
        Permission permission = permissionRepository.findByUuid(permissionUuid)
                .orElseThrow(() -> {
                    return new IamDomainException("Permission not found: " + permissionUuid);
                });

        iamDomainService.validatePermissionCanBeModifiedOrDeleted(permission);

        String actor = currentActorProvider.getCurrentActor();
        LocalDateTime now = LocalDateTime.now();

        Permission updated = Permission.builder()
                .id(permission.getId())
                .internalId(permission.getInternalId())
                .name(permission.getName())
                .resource(permission.getResource())
                .action(permission.getAction())
                .description(permission.getDescription())
                .isSystem(permission.getSystem())
                .isActive(false)
                .isDeleted(permission.getDeleted())
                .deletedAt(permission.getDeletedAt())
                .deletedBy(permission.getDeletedBy())
                .createdAt(permission.getCreatedAt())
                .createdBy(permission.getCreatedBy())
                .updatedAt(now)
                .updatedBy(actor)
                .version(permission.getVersion())
                .build();

        permissionRepository.save(updated);
    }
}

