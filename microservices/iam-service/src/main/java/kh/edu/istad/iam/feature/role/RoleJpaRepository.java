package kh.edu.istad.iam.feature.role;

import kh.edu.istad.iam.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleJpaRepository extends JpaRepository<Role, Long> {
}
