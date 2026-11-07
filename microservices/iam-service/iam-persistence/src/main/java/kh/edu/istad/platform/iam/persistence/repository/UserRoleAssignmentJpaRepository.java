package kh.edu.istad.platform.iam.persistence.repository;

import kh.edu.istad.platform.iam.persistence.entity.UserRoleAssignmentJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UserRoleAssignmentJpaRepository extends JpaRepository<UserRoleAssignmentJpaEntity, Long> {

    @Query("""
        SELECT a FROM UserRoleAssignmentJpaEntity a 
        WHERE a.userId = :userId 
          AND a.revokedAt IS NULL 
          AND (a.expiresAt IS NULL OR a.expiresAt > :now)
    """)
    List<UserRoleAssignmentJpaEntity> findActiveByUserId(
            @Param("userId") Long userId,
            @Param("now") LocalDateTime now
    );
}

