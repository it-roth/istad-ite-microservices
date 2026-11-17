package kh.edu.istad.platform.iam.infrastructure.adapter;

import kh.edu.istad.platform.iam.domain.port.out.AccountSecurityRepository;
import kh.edu.istad.platform.iam.domain.port.out.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class LoginAuditListener {
    private final AccountSecurityRepository audit;
    private final UserRepository users;

    @EventListener
    public void onSuccess(AuthenticationSuccessEvent event) {
        record(event.getAuthentication().getName(), true, "PASSWORD");
    }

    @EventListener
    public void onFailure(AbstractAuthenticationFailureEvent event) {
        record(event.getAuthentication().getName(), false, "PASSWORD");
    }

    private void record(String principal, boolean success, String method) {
        UUID userId = users.findByUsernameOrEmail(principal).map(u -> u.getId().getValue()).orElse(null);
        audit.recordLoginAttempt(userId, principal, success, method, null, null);
        if (userId != null) {
            audit.recordActivity(userId, success ? "login_success" : "login_failed",
                    success ? "Successful sign in" : "Failed sign in", principal, null);
        }
    }
}
