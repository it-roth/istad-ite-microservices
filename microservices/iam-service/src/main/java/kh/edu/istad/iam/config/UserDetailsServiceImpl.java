package kh.edu.istad.iam.config;

import kh.edu.istad.iam.feature.user.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserJpaRepository userJpaRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var existing = userJpaRepository.findByUsernameOrEmail(username, username).orElseThrow(
                () -> new UsernameNotFoundException("Username not found")
        );

        Set<GrantedAuthority> authorities = new HashSet<>();
        existing.getPermissions()
                .forEach(
                        permission -> {
                            authorities.add(
                                    new SimpleGrantedAuthority(permission.getPermission())
                            );
                        }
                );

        existing.getRoles()
                .forEach(
                        role -> {
                            authorities.add(
                                    new SimpleGrantedAuthority("ROLE_" + role.getRole())
                            );
                        }
                );

        return new User(
                existing.getUsername(),
                existing.getPassword(),
                existing.isEnabled(),
                existing.isAccountNonExpired(),
                existing.isCredentialsNonExpired(),
                existing.isAccountNonLocked(),
                authorities

        );
    }
}
