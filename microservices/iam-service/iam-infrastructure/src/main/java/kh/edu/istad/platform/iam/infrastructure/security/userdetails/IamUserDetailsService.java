package kh.edu.istad.platform.iam.infrastructure.security.userdetails;

import kh.edu.istad.platform.iam.domain.dto.AuthUserResult;
import kh.edu.istad.platform.iam.domain.usecase.LoadUserForAuthenticationUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class IamUserDetailsService implements UserDetailsService {

    private final LoadUserForAuthenticationUseCase loadUserForAuthenticationUseCase;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Authenticating user with username or email: {}", username);
        try {
            AuthUserResult authUser = loadUserForAuthenticationUseCase.execute(username);
            return new IamUserDetails(authUser);
        } catch (Exception ex) {
            log.warn("Authentication failed for username {}: {}", username, ex.getMessage());
            throw new UsernameNotFoundException("User not found or credentials invalid: " + username, ex);
        }
    }
}

