package kh.edu.istad.platform.iam.infrastructure.adapter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kh.edu.istad.platform.iam.domain.port.out.AccountSecurityRepository;
import kh.edu.istad.platform.iam.domain.port.out.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SessionTrackingFilter extends OncePerRequestFilter {
    private final AccountSecurityRepository sessions;
    private final UserRepository users;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        Authentication authentication = org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()
                && request.getSession(false) != null && !"anonymousUser".equals(authentication.getName())) {
            users.findByUsernameOrEmail(authentication.getName()).ifPresent(user -> sessions.registerSession(
                    user.getId().getValue(), request.getSession(false).getId(),
                    request.getHeader("User-Agent"), request.getHeader("User-Agent"),
                    request.getHeader("User-Agent"), clientIp(request)));
        }
        filterChain.doFilter(request, response);
    }

    private String clientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        String ip = (forwarded == null || forwarded.isBlank()) ? request.getRemoteAddr() : forwarded.split(",")[0].trim();
        if ("0:0:0:0:0:0:0:1".equals(ip) || "::1".equals(ip)) {
            return "127.0.0.1";
        }
        return ip;
    }
}
