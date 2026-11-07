package kh.edu.istad.platform.iam.domain.usecase;

import kh.edu.istad.platform.iam.domain.dto.PermissionResult;
import kh.edu.istad.platform.iam.domain.dto.UpdatePermissionCommand;
import kh.edu.istad.platform.iam.domain.entity.Permission;
import kh.edu.istad.platform.iam.domain.exception.IamDomainException;
import kh.edu.istad.platform.iam.domain.mapper.PermissionResultMapper;
import kh.edu.istad.platform.iam.domain.port.out.CurrentActorProvider;
import kh.edu.istad.platform.iam.domain.port.out.PermissionRepository;
import kh.edu.istad.platform.iam.domain.service.IamDomainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Slf4j
@RequiredArgsConstructor
public class UpdatePermissionUseCase {

    private final PermissionRepository permissionRepository;
    private final CurrentActorProvider currentActorProvider;
    private final IamDomainService iamDomainService;

    public PermissionResult execute(UpdatePermissionCommand command) {
        log.info("Updating permission UUID: {}", command.permissionUuid());
        Permission existing = permissionRepository.findByUuid(command.permissionUuid())
                .orElseThrow(() -> {
                    return new IamDomainException("Permission not found: " + command.permissionUuid());
                });

        iamDomainService.validatePermissionCanBeModifiedOrDeleted(existing);

        String actor = currentActorProvider.getCurrentActor();
        LocalDateTime now = LocalDateTime.now();

        Permission updated = Permission.builder()
                .id(existing.getId())
                .internalId(existing.getInternalId())
                .name(existing.getName())
                .resource(existing.getResource())
                .action(existing.getAction())
                .description(command.description())
                .isSystem(existing.getSystem())
                .isActive(existing.getActive())
                .isDeleted(existing.getDeleted())
                .deletedAt(existing.getDeletedAt())
                .deletedBy(existing.getDeletedBy())
                .createdAt(existing.getCreatedAt())
                .createdBy(existing.getCreatedBy())
                .updatedAt(now)
                .updatedBy(actor)
                .version(existing.getVersion())
                .build();

        return PermissionResultMapper.toResult(permissionRepository.save(updated));
    }
}

