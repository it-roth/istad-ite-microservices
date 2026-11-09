package kh.edu.istad.platform.iam.infrastructure.security.userdetails;

import kh.edu.istad.platform.iam.domain.dto.AuthUserResult;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
public class IamUserDetails implements UserDetails {

    private final UUID userUuid;
    private final String username;
    private final String email;
    private final String password;
    private final boolean enabled;
    private final boolean accountNonExpired;
    private final boolean accountNonLocked;
    private final boolean credentialsNonExpired;
    private final Set<GrantedAuthority> authorities;
    private final Set<String> roles;
    private final Set<String> permissions;

    public IamUserDetails(AuthUserResult authUser) {
        this.userUuid = authUser.uuid();
        this.username = authUser.username();
        this.email = authUser.email();
        this.password = authUser.passwordHash();
        this.enabled = Boolean.TRUE.equals(authUser.enabled());
        this.accountNonExpired = Boolean.TRUE.equals(authUser.accountNonExpired());
        this.accountNonLocked = Boolean.TRUE.equals(authUser.accountNonLocked());
        this.credentialsNonExpired = Boolean.TRUE.equals(authUser.credentialsNonExpired());

        Set<String> allAuths = authUser.authorities() != null
                ? authUser.authorities()
                : Collections.emptySet();

        this.authorities = allAuths.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toUnmodifiableSet());

        this.roles = allAuths.stream()
                .filter(a -> a.startsWith("ROLE_"))
                .map(a -> a.substring(5))
                .collect(Collectors.toUnmodifiableSet());

        this.permissions = allAuths.stream()
                .filter(a -> !a.startsWith("ROLE_"))
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
