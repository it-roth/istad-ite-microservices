package kh.edu.istad.platform.iam.persistence.repository;

import kh.edu.istad.platform.iam.persistence.entity.UserJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserJpaRepository extends JpaRepository<UserJpaEntity, Long> {

    Optional<UserJpaEntity> findByUuid(UUID uuid);

    Optional<UserJpaEntity> findByUsername(String username);

    Optional<UserJpaEntity> findByEmail(String email);

    @Query("SELECT u FROM UserJpaEntity u WHERE u.username = :input OR LOWER(u.email) = LOWER(:input)")
    Optional<UserJpaEntity> findByUsernameOrEmail(@Param("input") String input);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    @Query("""
                SELECT u FROM UserJpaEntity u
                WHERE (:query IS NULL OR :query = '' OR LOWER(u.username) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(u.email) LIKE LOWER(CONCAT('%', :query, '%')))
                  AND (:enabled IS NULL OR u.enabled = :enabled)
                  AND u.isDeleted = false
            """)
    Page<UserJpaEntity> findAllUsers(
            @Param("query") String query,
            @Param("enabled") Boolean enabled,
            Pageable pageable);
}
