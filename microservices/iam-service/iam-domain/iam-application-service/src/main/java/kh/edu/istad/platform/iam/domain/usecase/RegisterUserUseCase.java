package kh.edu.istad.platform.iam.domain.usecase;

import kh.edu.istad.platform.iam.domain.dto.RegisterUserCommand;
import kh.edu.istad.platform.iam.domain.dto.UserResult;
import kh.edu.istad.platform.iam.domain.entity.User;
import kh.edu.istad.platform.iam.domain.entity.UserRoleAssignment;
import kh.edu.istad.platform.iam.domain.exception.IamDomainException;
import kh.edu.istad.platform.iam.domain.mapper.UserResultMapper;
import kh.edu.istad.platform.iam.domain.port.out.PasswordHasher;
import kh.edu.istad.platform.iam.domain.port.out.RoleRepository;
import kh.edu.istad.platform.iam.domain.port.out.UserRepository;
import kh.edu.istad.platform.iam.domain.valueobject.UserId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class RegisterUserUseCase {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordHasher passwordHasher;

    @Transactional
    public UserResult execute(RegisterUserCommand command) {
        log.info("Executing RegisterUserUseCase for username: {}", command.username());
        if (userRepository.existsByUsername(command.username())) {
            throw new IamDomainException("Username '" + command.username() + "' is already taken");
        }
        if (userRepository.existsByEmail(command.email())) {
            throw new IamDomainException("Email '" + command.email() + "' is already registered");
        }
        LocalDateTime now = LocalDateTime.now();
        User user = User.builder()
                .id(new UserId(UUID.randomUUID()))
                .username(command.username())
                .email(command.email().toLowerCase().trim())
                .emailVerified(false)
                .passwordHash(passwordHasher.encode(command.rawPassword()))
                .givenName(command.givenName())
                .familyName(command.familyName())
                .locale(command.locale() == null ? "en" : command.locale())
                .phoneNumberVerified(false)
                .enabled(true)
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .failedLoginAttempts(0)
                .isDeleted(false)
                .createdAt(now)
                .createdBy("SELF_REGISTRATION")
                .updatedAt(now)
                .updatedBy("SELF_REGISTRATION")
                .version(0L)
                .build();

        User savedUser = userRepository.save(user);

        // Assign default 'USER' role
        roleRepository.findByName("USER").ifPresent(role -> {
            UserRoleAssignment assignment = UserRoleAssignment.builder()
                    .userId(savedUser.getInternalId())
                    .roleId(role.getInternalId())
                    .assignedAt(now)
                    .assignedBy("SYSTEM")
                    .reason("Default user role upon registration")
                    .build();
            userRepository.saveRoleAssignments(List.of(assignment));
        });

        return UserResultMapper.toResult(savedUser);
    }
}
