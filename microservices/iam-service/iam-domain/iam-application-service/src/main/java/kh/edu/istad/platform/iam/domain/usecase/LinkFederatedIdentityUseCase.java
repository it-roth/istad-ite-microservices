package kh.edu.istad.platform.iam.domain.usecase;

import kh.edu.istad.platform.iam.domain.dto.AuthUserResult;
import kh.edu.istad.platform.iam.domain.dto.FederatedIdentityCommand;
import kh.edu.istad.platform.iam.domain.dto.UserAuthoritiesResult;
import kh.edu.istad.platform.iam.domain.entity.Identity;
import kh.edu.istad.platform.iam.domain.entity.User;
import kh.edu.istad.platform.iam.domain.exception.IamDomainException;
import kh.edu.istad.platform.iam.domain.port.out.UserRepository;
import kh.edu.istad.platform.iam.domain.service.IamDomainService;
import kh.edu.istad.platform.iam.domain.valueobject.IdentityId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class LinkFederatedIdentityUseCase {

    private final UserRepository userRepository;
    private final GetUserAuthoritiesUseCase getUserAuthoritiesUseCase;
    private final IamDomainService iamDomainService;

    @Transactional
    public AuthUserResult execute(String username, FederatedIdentityCommand command) {
        User user = userRepository.findByUsernameOrEmail(username)
                .orElseThrow(() -> new IamDomainException("Signed-in user not found"));
        LocalDateTime now = LocalDateTime.now();

        Optional<Identity> existingIdentity = userRepository.findIdentityIncludingDeletedByProviderAndSubject(
                command.provider(),
                command.providerSubject()
        );
        List<Identity> existingIdentities = userRepository.findIdentitiesByUserId(user.getInternalId());
        if (existingIdentity.isPresent()) {
            Identity identity = existingIdentity.get();
            if (!user.getInternalId().equals(identity.getUserId())) {
                throw new IamDomainException("This social identity is already linked to another account");
            }
            if (Boolean.TRUE.equals(identity.getDeleted())) {
                iamDomainService.validateProviderNotAlreadyLinked(existingIdentities, command.provider());
                identity.setEmail(command.email());
                identity.setEmailVerified(Boolean.TRUE.equals(command.emailVerified()));
                identity.setDisplayName(command.displayName());
                identity.setUsername(command.username());
                identity.setPictureUrl(command.pictureUrl());
                identity.setAttributes(command.attributes());
                identity.setLinkedAt(now);
                identity.setDeleted(false);
                identity.setDeletedAt(null);
                identity.setDeletedBy(null);
                identity.setUpdatedBy("OIDC_" + command.provider());
            }
            identity.setLastLoginAt(now);
            identity.setUpdatedAt(now);
            userRepository.saveIdentity(identity);
        } else {
            iamDomainService.validateProviderNotAlreadyLinked(existingIdentities, command.provider());
            Identity identity = Identity.builder()
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
            userRepository.saveIdentity(identity);
        }

        UserAuthoritiesResult authorities = getUserAuthoritiesUseCase.execute(user.getId().value());
        return new AuthUserResult(
                user.getId().value(),
                user.getUsername(),
                user.getEmail(),
                user.getPasswordHash(),
                user.getEnabled(),
                user.getAccountNonExpired(),
                user.getAccountNonLocked(),
                user.getCredentialsNonExpired(),
                authorities.effectiveAuthorities()
        );
    }
}
