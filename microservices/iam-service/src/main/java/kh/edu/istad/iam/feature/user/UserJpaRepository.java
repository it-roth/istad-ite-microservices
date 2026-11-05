package kh.edu.istad.iam.feature.user;

import kh.edu.istad.iam.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserJpaRepository extends JpaRepository<User, Long> {
    boolean existsByUsername(String username);

    Optional<User> findByUsernameOrEmail(String username, String email);
}
