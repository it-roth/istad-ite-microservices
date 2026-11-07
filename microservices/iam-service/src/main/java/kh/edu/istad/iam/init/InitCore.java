package kh.edu.istad.iam.init;

import jakarta.annotation.PostConstruct;
import kh.edu.istad.iam.domain.Permission;
import kh.edu.istad.iam.domain.Role;
import kh.edu.istad.iam.domain.User;
import kh.edu.istad.iam.feature.permission.PermissionJpaRepository;
import kh.edu.istad.iam.feature.role.RoleJpaRepository;
import kh.edu.istad.iam.feature.user.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@Configuration
@RequiredArgsConstructor
public class InitCore {

    private final PermissionJpaRepository permissionJpaRepository;
    private final RoleJpaRepository roleJpaRepository;
    private final UserJpaRepository userJpaRepository;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    public void init(){
        Permission permission = Permission.builder()
                .permission("user:read:all")
                .description("Read all users")
                .build();

        Role role = Role.builder()
                .role("ADMIN")
                .description("jg yy jg hahh")
                .build();

        User user = User.builder()
                .familyName("Leng")
                .givenName("Saroth")
                .email("lengsaroth@gmail.com")
                .gender("Male")
                .emailVerified(true)
                .username("lengsaroth")
                .password(passwordEncoder.encode("supersecret"))
                .build();

        if(!userJpaRepository.existsByUsername("lengsaroth")){
            role = roleJpaRepository.save(role);
            permission = permissionJpaRepository.save(permission);
            user.setRoles(Set.of(role));
            user.setPermissions(Set.of(permission));
            userJpaRepository.save(user);
        }
    }
}
