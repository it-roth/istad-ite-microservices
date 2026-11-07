package kh.edu.istad.platform.iam.domain.usecase;

import kh.edu.istad.platform.iam.domain.dto.AssignRolesToUserCommand;
import kh.edu.istad.platform.iam.domain.dto.RoleAssignmentResult;
import kh.edu.istad.platform.iam.domain.entity.Role;
import kh.edu.istad.platform.iam.domain.entity.User;
import kh.edu.istad.platform.iam.domain.entity.UserRoleAssignment;
import kh.edu.istad.platform.iam.domain.exception.IamDomainException;
import kh.edu.istad.platform.iam.domain.port.out.CurrentActorProvider;
import kh.edu.istad.platform.iam.domain.port.out.RoleRepository;
import kh.edu.istad.platform.iam.domain.port.out.UserRepository;
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
public class AssignRolesToUserUseCase {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final CurrentActorProvider currentActorProvider;
    private final IamDomainService iamDomainService;

    public List<RoleAssignmentResult> execute(AssignRolesToUserCommand command) {
        log.info("Assigning roles to user UUID: {}", command.userUuid());
        User user = userRepository.findByUuid(command.userUuid())
                .orElseThrow(() -> new IamDomainException("User not found: " + command.userUuid()));

        List<Role> roles = roleRepository.findAllByUuids(command.roleUuids());
        String actor = currentActorProvider.getCurrentActor();
        LocalDateTime now = LocalDateTime.now();

        List<UserRoleAssignment> assignments = new ArrayList<>();
        List<RoleAssignmentResult> results = new ArrayList<>();

        for (Role role : roles) {
            iamDomainService.validateRoleCanBeAssigned(role);
            UserRoleAssignment assignment = UserRoleAssignment.builder()
                    .userId(user.getInternalId())
                    .roleId(role.getInternalId())
                    .assignedAt(now)
                    .assignedBy(actor)
                    .expiresAt(command.expiresAt())
                    .reason(command.reason())
                    .build();
            assignments.add(assignment);
            results.add(new RoleAssignmentResult(role.getId().value(), role.getName(), now, actor, command.expiresAt()));
        }

        userRepository.saveRoleAssignments(assignments);
        return results;
    }
}


