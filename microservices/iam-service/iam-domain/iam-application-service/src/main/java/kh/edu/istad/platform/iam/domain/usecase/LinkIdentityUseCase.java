package kh.edu.istad.platform.iam.domain.usecase;

import kh.edu.istad.platform.iam.domain.dto.FederatedIdentityCommand;
import kh.edu.istad.platform.iam.domain.dto.IdentityResult;
import kh.edu.istad.platform.iam.domain.entity.Identity;
import kh.edu.istad.platform.iam.domain.entity.User;
import kh.edu.istad.platform.iam.domain.exception.IamDomainException;
import kh.edu.istad.platform.iam.domain.mapper.IdentityResultMapper;
import kh.edu.istad.platform.iam.domain.port.out.CurrentActorProvider;
import kh.edu.istad.platform.iam.domain.port.out.UserRepository;
import kh.edu.istad.platform.iam.domain.service.IamDomainService;
import kh.edu.istad.platform.iam.domain.valueobject.IdentityId;
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
public class LinkIdentityUseCase {

    private final UserRepository userRepository;
    private final CurrentActorProvider currentActorProvider;
    private final IamDomainService iamDomainService;

    @Transactional
    public IdentityResult execute(UUID userUuid, FederatedIdentityCommand command) {
        log.info("Linking identity {} to user UUID: {}", command.provider(), userUuid);
        User user = userRepository.findByUuid(userUuid)
                .orElseThrow(() -> {
                    return new IamDomainException("User not found: " + userUuid);
                });

        List<Identity> existingIdentities = userRepository.findIdentitiesByUserId(user.getInternalId());
        String actor = currentActorProvider.getCurrentActor();
        LocalDateTime now = LocalDateTime.now();

        var existingIdentity = userRepository.findIdentityIncludingDeletedByProviderAndSubject(
                command.provider(),
                command.providerSubject()
        );
        if (existingIdentity.isPresent()) {
            Identity identity = existingIdentity.get();
            if (!user.getInternalId().equals(identity.getUserId())) {
                throw new IamDomainException("This social identity is already linked to another account");
            }
            if (!Boolean.TRUE.equals(identity.getDeleted())) {
                throw new IamDomainException("This social identity is already linked to this account");
            }

            iamDomainService.validateProviderNotAlreadyLinked(existingIdentities, command.provider());
            identity.setEmail(command.email());
            identity.setEmailVerified(Boolean.TRUE.equals(command.emailVerified()));
            identity.setDisplayName(command.displayName());
            identity.setUsername(command.username());
            identity.setPictureUrl(command.pictureUrl());
            identity.setAttributes(command.attributes());
            identity.setLinkedAt(now);
            identity.setLastLoginAt(now);
            identity.setDeleted(false);
            identity.setDeletedAt(null);
            identity.setDeletedBy(null);
            identity.setUpdatedAt(now);
            identity.setUpdatedBy(actor);
            return IdentityResultMapper.toResult(userRepository.saveIdentity(identity));
        }

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
                .createdBy(actor)
                .updatedAt(now)
                .updatedBy(actor)
                .version(0L)
                .build();

        return IdentityResultMapper.toResult(userRepository.saveIdentity(identity));
    }
}
