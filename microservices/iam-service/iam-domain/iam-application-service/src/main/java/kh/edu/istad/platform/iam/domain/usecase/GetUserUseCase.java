package kh.edu.istad.platform.iam.domain.usecase;

import kh.edu.istad.platform.iam.domain.dto.UserResult;
import kh.edu.istad.platform.iam.domain.entity.User;
import kh.edu.istad.platform.iam.domain.exception.IamDomainException;
import kh.edu.istad.platform.iam.domain.mapper.UserResultMapper;
import kh.edu.istad.platform.iam.domain.port.out.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class GetUserUseCase {

    private final UserRepository userRepository;

    public UserResult execute(UUID userUuid) {
        log.info("Fetching user by UUID: {}", userUuid);
        User user = userRepository.findByUuid(userUuid)
                .orElseThrow(() -> new IamDomainException("User not found with uuid: " + userUuid));
        return UserResultMapper.toResult(user);
    }
}


