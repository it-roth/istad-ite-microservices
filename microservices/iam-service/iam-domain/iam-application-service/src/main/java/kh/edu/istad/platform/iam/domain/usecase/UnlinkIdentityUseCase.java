package kh.edu.istad.platform.iam.domain.usecase;

import kh.edu.istad.platform.iam.domain.entity.Identity;
import kh.edu.istad.platform.iam.domain.entity.User;
import kh.edu.istad.platform.iam.domain.exception.IamDomainException;
import kh.edu.istad.platform.iam.domain.port.out.CurrentActorProvider;
import kh.edu.istad.platform.iam.domain.port.out.UserRepository;
import kh.edu.istad.platform.iam.domain.service.IamDomainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class UnlinkIdentityUseCase {

    private final UserRepository userRepository;
    private final CurrentActorProvider currentActorProvider;
    private final IamDomainService iamDomainService;

    public void execute(UUID userUuid, UUID identityUuid) {
        log.info("Unlinking identity UUID: {} for user UUID: {}", identityUuid, userUuid);
        User user = userRepository.findByUuid(userUuid)
                .orElseThrow(() -> {
                    return new IamDomainException("User not found: " + userUuid);
                });

        List<Identity> activeIdentities = userRepository.findIdentitiesByUserId(user.getInternalId());
        iamDomainService.validateIdentityCanBeUnlinked(user, activeIdentities.size());

        Identity target = activeIdentities.stream()
                .filter(id -> {
                    return identityUuid.equals(id.getId().value());
                })
                .findFirst()
                .orElseThrow(() -> {
                    return new IamDomainException("Identity not linked to user: " + identityUuid);
                });

        String actor = currentActorProvider.getCurrentActor();
        LocalDateTime now = LocalDateTime.now();

        target.setDeleted(true);
        target.setDeletedAt(now);
        target.setDeletedBy(actor);
        target.setUpdatedAt(now);
        target.setUpdatedBy(actor);

        userRepository.saveIdentity(target);
    }
}

