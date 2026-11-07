package kh.edu.istad.platform.iam.domain.usecase;

import kh.edu.istad.platform.iam.domain.entity.Permission;
import kh.edu.istad.platform.iam.domain.entity.User;
import kh.edu.istad.platform.iam.domain.entity.UserPermissionAssignment;
import kh.edu.istad.platform.iam.domain.exception.IamDomainException;
import kh.edu.istad.platform.iam.domain.port.out.CurrentActorProvider;
import kh.edu.istad.platform.iam.domain.port.out.PermissionRepository;
import kh.edu.istad.platform.iam.domain.port.out.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class RevokePermissionFromUserUseCase {

    private final UserRepository userRepository;
    private final PermissionRepository permissionRepository;
    private final CurrentActorProvider currentActorProvider;

    public void execute(UUID userUuid, UUID permissionUuid, String reason) {
        log.info("Revoking permission UUID: {} from user UUID: {}", permissionUuid, userUuid);
        User user = userRepository.findByUuid(userUuid)
                .orElseThrow(() -> new IamDomainException("User not found: " + userUuid));
        Permission permission = permissionRepository.findByUuid(permissionUuid)
                .orElseThrow(() -> new IamDomainException("Permission not found: " + permissionUuid));

        String actor = currentActorProvider.getCurrentActor();
        List<UserPermissionAssignment> assignments = userRepository.findActivePermissionAssignments(user.getInternalId());

        boolean updated = false;
        for (UserPermissionAssignment assignment : assignments) {
            if (permission.getInternalId().equals(assignment.getPermissionId())) {
                assignment.revoke(actor, reason);
                updated = true;
            }
        }

        if (updated) {
            userRepository.savePermissionAssignments(assignments);
        }
    }
}


