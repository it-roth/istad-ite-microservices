package kh.edu.istad.platform.iam.domain.usecase;

import kh.edu.istad.platform.iam.domain.dto.GrantPermissionsToUserCommand;
import kh.edu.istad.platform.iam.domain.dto.PermissionGrantResult;
import kh.edu.istad.platform.iam.domain.entity.Permission;
import kh.edu.istad.platform.iam.domain.entity.User;
import kh.edu.istad.platform.iam.domain.entity.UserPermissionAssignment;
import kh.edu.istad.platform.iam.domain.exception.IamDomainException;
import kh.edu.istad.platform.iam.domain.port.out.CurrentActorProvider;
import kh.edu.istad.platform.iam.domain.port.out.PermissionRepository;
import kh.edu.istad.platform.iam.domain.port.out.UserRepository;
import kh.edu.istad.platform.iam.domain.service.IamDomainService;
import kh.edu.istad.platform.iam.domain.valueobject.PermissionEffect;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Component
@Slf4j
@RequiredArgsConstructor
public class GrantPermissionsToUserUseCase {

    private final UserRepository userRepository;
    private final PermissionRepository permissionRepository;
    private final CurrentActorProvider currentActorProvider;
    private final IamDomainService iamDomainService;

    public List<PermissionGrantResult> execute(GrantPermissionsToUserCommand command) {
        PermissionEffect effect;
        try {
            effect = PermissionEffect.valueOf(command.effect().toUpperCase(Locale.ROOT));
        } catch (NullPointerException | IllegalArgumentException ex) {
            throw new IamDomainException("Permission effect must be either GRANT or DENY", ex);
        }

        log.info("Granting permissions to user UUID: {}", command.userUuid());
        User user = userRepository.findByUuid(command.userUuid())
                .orElseThrow(() -> new IamDomainException("User not found: " + command.userUuid()));

        List<Permission> permissions = permissionRepository.findAllByUuids(command.permissionUuids());
        String actor = currentActorProvider.getCurrentActor();
        LocalDateTime now = LocalDateTime.now();

        List<UserPermissionAssignment> assignments = new ArrayList<>();
        List<PermissionGrantResult> results = new ArrayList<>();

        for (Permission permission : permissions) {
            iamDomainService.validatePermissionCanBeAssigned(permission);
            UserPermissionAssignment assignment = UserPermissionAssignment.builder()
                    .userId(user.getInternalId())
                    .permissionId(permission.getInternalId())
                    .effect(effect)
                    .assignedAt(now)
                    .assignedBy(actor)
                    .expiresAt(command.expiresAt())
                    .reason(command.reason())
                    .build();
            assignments.add(assignment);
            results.add(new PermissionGrantResult(permission.getId().value(), permission.getName(), effect.name(), now, actor, command.expiresAt()));
        }

        userRepository.savePermissionAssignments(assignments);
        return results;
    }
}
