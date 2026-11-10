package kh.edu.istad.platform.iam.infrastructure.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Component
public class CustomJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        Collection<GrantedAuthority> authorities = extractAuthorities(jwt);
        String principalClaimName = jwt.getSubject();
        return new JwtAuthenticationToken(jwt, authorities, principalClaimName);
    }

    private Collection<GrantedAuthority> extractAuthorities(Jwt jwt) {
        List<GrantedAuthority> authorities = new ArrayList<>();

        // 1. Roles -> ROLE_<roleName>
        Object rolesObj = jwt.getClaims().get("roles");
        if (rolesObj instanceof Collection<?> rolesCollection) {
            for (Object role : rolesCollection) {
                if (role != null) {
                    String roleName = role.toString().trim();
                    if (!roleName.startsWith("ROLE_")) {
                        roleName = "ROLE_" + roleName;
                    }
                    authorities.add(new SimpleGrantedAuthority(roleName));
                }
            }
        }

        // 2. Permissions / authorities -> resource:action
        Object permsObj = jwt.getClaims().get("permissions");
        if (permsObj instanceof Collection<?> permsCollection) {
            for (Object perm : permsCollection) {
                if (perm != null) {
                    authorities.add(new SimpleGrantedAuthority(perm.toString().trim()));
                }
            }
        }

        Object authsObj = jwt.getClaims().get("authorities");
        if (authsObj instanceof Collection<?> authsCollection) {
            for (Object auth : authsCollection) {
                if (auth != null) {
                    authorities.add(new SimpleGrantedAuthority(auth.toString().trim()));
                }
            }
        }

        // 3. Scopes -> SCOPE_<scope>
        Object scopeObj = jwt.getClaims().get("scope");
        if (scopeObj instanceof String scopeStr) {
            for (String scope : scopeStr.split("\\s+")) {
                if (!scope.isBlank()) {
                    authorities.add(new SimpleGrantedAuthority("SCOPE_" + scope));
                }
            }
        } else if (scopeObj instanceof Collection<?> scopeColl) {
            for (Object scope : scopeColl) {
                if (scope != null) {
                    authorities.add(new SimpleGrantedAuthority("SCOPE_" + scope));
                }
            }
        }

        return authorities;
    }
}
