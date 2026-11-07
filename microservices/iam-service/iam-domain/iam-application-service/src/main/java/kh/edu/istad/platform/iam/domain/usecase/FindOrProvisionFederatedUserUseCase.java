package kh.edu.istad.platform.iam.domain.usecase;

import kh.edu.istad.platform.iam.domain.dto.AuthUserResult;
import kh.edu.istad.platform.iam.domain.dto.FederatedIdentityCommand;
import kh.edu.istad.platform.iam.domain.dto.UserAuthoritiesResult;
import kh.edu.istad.platform.iam.domain.entity.Identity;
import kh.edu.istad.platform.iam.domain.entity.User;
import kh.edu.istad.platform.iam.domain.entity.UserRoleAssignment;
import kh.edu.istad.platform.iam.domain.port.out.RoleRepository;
import kh.edu.istad.platform.iam.domain.port.out.UserRepository;
import kh.edu.istad.platform.iam.domain.valueobject.IdentityId;
import kh.edu.istad.platform.iam.domain.valueobject.UserId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class FindOrProvisionFederatedUserUseCase {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final GetUserAuthoritiesUseCase getUserAuthoritiesUseCase;

    public AuthUserResult execute(FederatedIdentityCommand command) {
        log.info("Processing federated login for provider: {}, subject: {}", command.provider(), command.providerSubject());
        LocalDateTime now = LocalDateTime.now();

        // 1. Check if identity already exists
        Optional<Identity> existingIdentity = userRepository.findIdentityByProviderAndSubject(command.provider(), command.providerSubject());

        User user;
        if (existingIdentity.isPresent()) {
            Identity identity = existingIdentity.get();
            identity.setLastLoginAt(now);
            identity.setUpdatedAt(now);
            userRepository.saveIdentity(identity);

            user = userRepository.findById(identity.getUserId())
                    .orElseThrow(() -> {
                        return new IllegalStateException("User missing for identity: " + identity.getId().value());
                    });
        } else {
            // 2. Check if a user with this email already exists to link, otherwise provision a new user
            Optional<User> existingUserByEmail = (command.email() != null && !command.email().isBlank())
                    ? userRepository.findByEmail(command.email().toLowerCase().trim())
                    : Optional.empty();

            if (existingUserByEmail.isPresent()) {
                user = existingUserByEmail.get();
            } else {
                String username = command.username() != null ? command.username() : (command.provider().name().toLowerCase() + "_" + command.providerSubject());
                if (userRepository.existsByUsername(username)) {
                    username = username + "_" + UUID.randomUUID().toString().substring(0, 8);
                }

                String givenName = command.displayName() != null ? command.displayName() : username;
                String familyName = "";

                user = User.builder()
                        .id(new UserId(UUID.randomUUID()))
                        .username(username)
                        .email(command.email() != null ? command.email().toLowerCase().trim() : username + "@social.local")
                        .emailVerified(Boolean.TRUE.equals(command.emailVerified()))
                        .passwordHash(null) // null for social-only users
                        .givenName(givenName)
                        .familyName(familyName)
                        .pictureUrl(command.pictureUrl())
                        .locale("en")
                        .enabled(true)
                        .accountNonExpired(true)
                        .accountNonLocked(true)
                        .credentialsNonExpired(true)
                        .failedLoginAttempts(0)
                        .lastLoginAt(now)
                        .isDeleted(false)
                        .createdAt(now)
                        .createdBy("OIDC_" + command.provider())
                        .updatedAt(now)
                        .updatedBy("OIDC_" + command.provider())
                        .version(0L)
                        .build();

                user = userRepository.save(user);

                // Assign default USER role
                final User finalUser = user;
                roleRepository.findByName("USER").ifPresent(role -> {
                    UserRoleAssignment assignment = UserRoleAssignment.builder()
                            .userId(finalUser.getInternalId())
                            .roleId(role.getInternalId())
                            .assignedAt(now)
                            .assignedBy("SYSTEM")
                            .reason("Default role for federated user")
                            .build();
                    userRepository.saveRoleAssignments(List.of(assignment));
                });
            }

            // Create new Identity row
            Identity newIdentity = Identity.builder()
                    .id(new IdentityId(UUID.randomUUID()))
                    .userId(user.getInternalId())
                    .provider(command.provider())
                    .providerSubject(command.providerSubject())
                    .email(command.email())
                    .emailVerified(Boolean.TRUE.equals(command.emailVerified()))
                    .displayName(command.displayName())
                    .username(command.username())
                    .pictureUrl(command.pictureUrl())
                    .attributes(command.attributes())
                    .linkedAt(now)
                    .lastLoginAt(now)
                    .isDeleted(false)
                    .createdAt(now)
                    .createdBy("OIDC_" + command.provider())
                    .updatedAt(now)
                    .updatedBy("OIDC_" + command.provider())
                    .version(0L)
                    .build();

            userRepository.saveIdentity(newIdentity);
        }

        UserAuthoritiesResult authResult = getUserAuthoritiesUseCase.execute(user.getId().value());

        return new AuthUserResult(
                user.getId().value(),
                user.getUsername(),
                user.getEmail(),
                user.getPasswordHash(),
                user.getEnabled(),
                user.getAccountNonExpired(),
                user.getAccountNonLocked(),
                user.getCredentialsNonExpired(),
                authResult.effectiveAuthorities()
        );
    }
}

