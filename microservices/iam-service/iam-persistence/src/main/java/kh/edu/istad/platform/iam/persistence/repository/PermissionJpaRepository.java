package kh.edu.istad.platform.iam.persistence.repository;

import kh.edu.istad.platform.iam.persistence.entity.PermissionJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PermissionJpaRepository extends JpaRepository<PermissionJpaEntity, Long> {

    Optional<PermissionJpaEntity> findByUuid(UUID uuid);

    Optional<PermissionJpaEntity> findByName(String name);

    List<PermissionJpaEntity> findAllByUuidIn(Collection<UUID> uuids);

    boolean existsByName(String name);

    @Query("""
        SELECT p FROM PermissionJpaEntity p 
        WHERE (:resource IS NULL OR :resource = '' OR LOWER(p.resource) = LOWER(:resource))
          AND (:action IS NULL OR :action = '' OR LOWER(p.action) = LOWER(:action))
          AND p.isDeleted = false
    """)
    Page<PermissionJpaEntity> findAllPermissions(
            @Param("resource") String resource,
            @Param("action") String action,
            Pageable pageable
    );
}

