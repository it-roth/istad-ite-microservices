package kh.edu.istad.platform.iam.infrastructure.adapter;

import kh.edu.istad.platform.iam.domain.port.out.CurrentActorProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class CurrentActorProviderAdapter implements CurrentActorProvider {

    private static final String DEFAULT_SYSTEM_ACTOR = "SYSTEM";

    @Override
    public String getCurrentActor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return DEFAULT_SYSTEM_ACTOR;
        }

        String name = authentication.getName();
        if (name == null || name.isBlank() || "anonymousUser".equalsIgnoreCase(name)) {
            return DEFAULT_SYSTEM_ACTOR;
        }

        return name;
    }
}

