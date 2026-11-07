package kh.edu.istad.platform.iam.domain.usecase;

import kh.edu.istad.platform.iam.domain.dto.IdentityResult;
import kh.edu.istad.platform.iam.domain.entity.Identity;
import kh.edu.istad.platform.iam.domain.entity.User;
import kh.edu.istad.platform.iam.domain.exception.IamDomainException;
import kh.edu.istad.platform.iam.domain.mapper.IdentityResultMapper;
import kh.edu.istad.platform.iam.domain.port.out.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class ListUserIdentitiesUseCase {

    private final UserRepository userRepository;

    public List<IdentityResult> execute(UUID userUuid) {
        log.info("Listing identities for user UUID: {}", userUuid);
        User user = userRepository.findByUuid(userUuid)
                .orElseThrow(() -> {
                    return new IamDomainException("User not found: " + userUuid);
                });

        List<Identity> identities = userRepository.findIdentitiesByUserId(user.getInternalId());
        return identities.stream()
                .map(IdentityResultMapper::toResult)
                .toList();
    }
}

