package kh.edu.istad.platform.iam.domain.usecase;

import kh.edu.istad.platform.iam.domain.dto.RoleResult;
import kh.edu.istad.platform.iam.domain.dto.UpdateRoleCommand;
import kh.edu.istad.platform.iam.domain.entity.Role;
import kh.edu.istad.platform.iam.domain.exception.IamDomainException;
import kh.edu.istad.platform.iam.domain.mapper.RoleResultMapper;
import kh.edu.istad.platform.iam.domain.port.out.CurrentActorProvider;
import kh.edu.istad.platform.iam.domain.port.out.RoleRepository;
import kh.edu.istad.platform.iam.domain.service.IamDomainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Slf4j
@RequiredArgsConstructor
public class UpdateRoleUseCase {

    private final RoleRepository roleRepository;
    private final CurrentActorProvider currentActorProvider;
    private final IamDomainService iamDomainService;

    public RoleResult execute(UpdateRoleCommand command) {
        log.info("Updating role UUID: {}", command.roleUuid());
        Role existing = roleRepository.findByUuid(command.roleUuid())
                .orElseThrow(() -> {
                    return new IamDomainException("Role not found: " + command.roleUuid());
                });

        iamDomainService.validateRoleCanBeModifiedOrDeleted(existing);

        String actor = currentActorProvider.getCurrentActor();
        LocalDateTime now = LocalDateTime.now();

        Role updated = Role.builder()
                .id(existing.getId())
                .internalId(existing.getInternalId())
                .name(existing.getName())
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

        return RoleResultMapper.toResult(roleRepository.save(updated));
    }
}

