package kh.edu.istad.platform.iam.persistence.repository;

import kh.edu.istad.platform.iam.persistence.entity.UserPermissionAssignmentJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UserPermissionAssignmentJpaRepository extends JpaRepository<UserPermissionAssignmentJpaEntity, Long> {

    @Query("""
        SELECT a FROM UserPermissionAssignmentJpaEntity a 
        WHERE a.userId = :userId 
          AND a.revokedAt IS NULL 
          AND (a.expiresAt IS NULL OR a.expiresAt > :now)
    """)
    List<UserPermissionAssignmentJpaEntity> findActiveByUserId(
            @Param("userId") Long userId,
            @Param("now") LocalDateTime now
    );
}

