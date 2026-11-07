package kh.edu.istad.platform.iam.domain.usecase;

import kh.edu.istad.platform.iam.domain.dto.CreateUserCommand;
import kh.edu.istad.platform.iam.domain.dto.UserResult;
import kh.edu.istad.platform.iam.domain.entity.Role;
import kh.edu.istad.platform.iam.domain.entity.User;
import kh.edu.istad.platform.iam.domain.entity.UserRoleAssignment;
import kh.edu.istad.platform.iam.domain.exception.IamDomainException;
import kh.edu.istad.platform.iam.domain.mapper.UserResultMapper;
import kh.edu.istad.platform.iam.domain.port.out.CurrentActorProvider;
import kh.edu.istad.platform.iam.domain.port.out.PasswordHasher;
import kh.edu.istad.platform.iam.domain.port.out.RoleRepository;
import kh.edu.istad.platform.iam.domain.port.out.UserRepository;
import kh.edu.istad.platform.iam.domain.service.IamDomainService;
import kh.edu.istad.platform.iam.domain.valueobject.UserId;
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
public class CreateUserUseCase {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordHasher passwordHasher;
    private final CurrentActorProvider currentActorProvider;
    private final IamDomainService iamDomainService;

    public UserResult execute(CreateUserCommand command) {
        log.info("Executing CreateUserUseCase for username: {}", command.username());
        if (userRepository.existsByUsername(command.username())) {
            throw new IamDomainException("Username '" + command.username() + "' is already taken");
        }
        if (userRepository.existsByEmail(command.email())) {
            throw new IamDomainException("Email '" + command.email() + "' is already registered");
        }

        String actor = currentActorProvider.getCurrentActor();
        LocalDateTime now = LocalDateTime.now();

        User user = User.builder()
                .id(new UserId(UUID.randomUUID()))
                .username(command.username())
                .email(command.email().toLowerCase().trim())
                .emailVerified(true)
                .passwordHash(passwordHasher.encode(command.rawPassword()))
                .givenName(command.givenName())
                .familyName(command.familyName())
                .middleName(command.middleName())
                .nickname(command.nickname())
                .gender(command.gender())
                .birthdate(command.birthdate())
                .locale(command.locale() == null ? "en" : command.locale())
                .phoneNumber(command.phoneNumber())
                .phoneNumberVerified(false)
                .addressStreetAddress(command.streetAddress())
                .addressLocality(command.locality())
                .addressRegion(command.region())
                .addressPostalCode(command.postalCode())
                .addressCountry(command.country())
                .enabled(true)
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .failedLoginAttempts(0)
                .isDeleted(false)
                .createdAt(now)
                .createdBy(actor)
                .updatedAt(now)
                .updatedBy(actor)
                .version(0L)
                .build();

        User savedUser = userRepository.save(user);

        // Assign initial roles if specified
        if (command.roleUuids() != null && !command.roleUuids().isEmpty()) {
            List<Role> roles = roleRepository.findAllByUuids(command.roleUuids());
            List<UserRoleAssignment> assignments = new ArrayList<>();
            for (Role role : roles) {
                iamDomainService.validateRoleCanBeAssigned(role);
                assignments.add(UserRoleAssignment.builder()
                        .userId(savedUser.getInternalId())
                        .roleId(role.getInternalId())
                        .assignedAt(now)
                        .assignedBy(actor)
                        .reason("Assigned during user creation")
                        .build());
            }
            if (!assignments.isEmpty()) {
                userRepository.saveRoleAssignments(assignments);
            }
        }

        return UserResultMapper.toResult(savedUser);
    }
}

