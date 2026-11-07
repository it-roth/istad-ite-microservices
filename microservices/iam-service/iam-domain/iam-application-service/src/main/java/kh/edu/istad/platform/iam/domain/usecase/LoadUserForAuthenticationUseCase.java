package kh.edu.istad.platform.iam.domain.usecase;

import kh.edu.istad.platform.iam.domain.dto.AuthUserResult;
import kh.edu.istad.platform.iam.domain.dto.UserAuthoritiesResult;
import kh.edu.istad.platform.iam.domain.entity.User;
import kh.edu.istad.platform.iam.domain.exception.IamDomainException;
import kh.edu.istad.platform.iam.domain.port.out.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class LoadUserForAuthenticationUseCase {

    private final UserRepository userRepository;
    private final GetUserAuthoritiesUseCase getUserAuthoritiesUseCase;

    public AuthUserResult execute(String usernameOrEmail) {
        log.info("Loading user for authentication: {}", usernameOrEmail);
        User user = userRepository.findByUsernameOrEmail(usernameOrEmail)
                .orElseThrow(() -> {
                    return new IamDomainException("Invalid username or password");
                });

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

