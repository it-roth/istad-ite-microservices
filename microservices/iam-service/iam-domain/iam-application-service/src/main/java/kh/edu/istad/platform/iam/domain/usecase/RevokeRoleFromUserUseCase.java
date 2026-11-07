package kh.edu.istad.platform.iam.domain.usecase;

import kh.edu.istad.platform.iam.domain.entity.Role;
import kh.edu.istad.platform.iam.domain.entity.User;
import kh.edu.istad.platform.iam.domain.entity.UserRoleAssignment;
import kh.edu.istad.platform.iam.domain.exception.IamDomainException;
import kh.edu.istad.platform.iam.domain.port.out.CurrentActorProvider;
import kh.edu.istad.platform.iam.domain.port.out.RoleRepository;
import kh.edu.istad.platform.iam.domain.port.out.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class RevokeRoleFromUserUseCase {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final CurrentActorProvider currentActorProvider;

    public void execute(UUID userUuid, UUID roleUuid, String reason) {
        log.info("Revoking role UUID: {} from user UUID: {}", roleUuid, userUuid);
        User user = userRepository.findByUuid(userUuid)
                .orElseThrow(() -> new IamDomainException("User not found: " + userUuid));
        Role role = roleRepository.findByUuid(roleUuid)
                .orElseThrow(() -> new IamDomainException("Role not found: " + roleUuid));

        String actor = currentActorProvider.getCurrentActor();
        List<UserRoleAssignment> assignments = userRepository.findActiveRoleAssignments(user.getInternalId());

        boolean updated = false;
        for (UserRoleAssignment assignment : assignments) {
            if (role.getInternalId().equals(assignment.getRoleId())) {
                assignment.revoke(actor, reason);
                updated = true;
            }
        }

        if (updated) {
            userRepository.saveRoleAssignments(assignments);
        }
    }
}


