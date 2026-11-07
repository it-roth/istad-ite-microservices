package kh.edu.istad.platform.iam.persistence.repository;

import kh.edu.istad.platform.iam.persistence.entity.RolePermissionAssignmentJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Repository
public interface RolePermissionAssignmentJpaRepository extends JpaRepository<RolePermissionAssignmentJpaEntity, Long> {

    @Query("""
        SELECT a FROM RolePermissionAssignmentJpaEntity a 
        WHERE a.roleId = :roleId 
          AND a.revokedAt IS NULL 
          AND (a.expiresAt IS NULL OR a.expiresAt > :now)
    """)
    List<RolePermissionAssignmentJpaEntity> findActiveByRoleId(
            @Param("roleId") Long roleId,
            @Param("now") LocalDateTime now
    );

    @Query("""
        SELECT a FROM RolePermissionAssignmentJpaEntity a 
        WHERE a.roleId IN :roleIds 
          AND a.revokedAt IS NULL 
          AND (a.expiresAt IS NULL OR a.expiresAt > :now)
    """)
    List<RolePermissionAssignmentJpaEntity> findActiveByRoleIds(
            @Param("roleIds") Collection<Long> roleIds,
            @Param("now") LocalDateTime now
    );
}

