package kh.edu.istad.platform.iam.infrastructure.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.IOException;
import java.util.Locale;

@Controller
@Slf4j
public class OAuth2LinkController {

    @GetMapping("/oauth2/link/{provider}")
    public void initiateLink(
            @PathVariable("provider") String provider,
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            log.warn("Unauthenticated attempt to link provider {}", provider);
            response.sendRedirect("/login");
            return;
        }

        try {
            String currentUsername = auth.getName();
            HttpSession session = request.getSession(true);
            session.setAttribute("iam.link-user", currentUsername);
            log.info("Initiating identity link for user {} with provider {}", currentUsername, provider);
            response.sendRedirect("/oauth2/authorization/" + provider.toLowerCase(Locale.ROOT));
        } catch (Exception ex) {
            log.error("Failed to initiate identity link for provider {}: {}", provider, ex.getMessage());
            response.sendRedirect("/account/linked-accounts?error=link_failed");
        }
    }
}
