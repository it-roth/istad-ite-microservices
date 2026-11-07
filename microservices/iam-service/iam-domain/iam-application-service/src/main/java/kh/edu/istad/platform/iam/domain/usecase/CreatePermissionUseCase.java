package kh.edu.istad.platform.iam.domain.usecase;

import kh.edu.istad.platform.iam.domain.dto.CreatePermissionCommand;
import kh.edu.istad.platform.iam.domain.dto.PermissionResult;
import kh.edu.istad.platform.iam.domain.entity.Permission;
import kh.edu.istad.platform.iam.domain.exception.IamDomainException;
import kh.edu.istad.platform.iam.domain.mapper.PermissionResultMapper;
import kh.edu.istad.platform.iam.domain.port.out.CurrentActorProvider;
import kh.edu.istad.platform.iam.domain.port.out.PermissionRepository;
import kh.edu.istad.platform.iam.domain.valueobject.PermissionId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class CreatePermissionUseCase {

    private final PermissionRepository permissionRepository;
    private final CurrentActorProvider currentActorProvider;

    public PermissionResult execute(CreatePermissionCommand command) {
        String name = (command.resource() + ":" + command.action()).toLowerCase().trim();
        log.info("Creating permission: {}", name);

        if (permissionRepository.existsByName(name)) {
            throw new IamDomainException("Permission '" + name + "' already exists");
        }

        String actor = currentActorProvider.getCurrentActor();
        LocalDateTime now = LocalDateTime.now();

        Permission permission = Permission.builder()
                .id(new PermissionId(UUID.randomUUID()))
                .name(name)
                .resource(command.resource().toLowerCase().trim())
                .action(command.action().toLowerCase().trim())
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

        return PermissionResultMapper.toResult(permissionRepository.save(permission));
    }
}

