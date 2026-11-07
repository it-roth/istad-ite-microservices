package kh.edu.istad.platform.iam.persistence.repository;

import kh.edu.istad.platform.iam.persistence.entity.RoleJpaEntity;
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
public interface RoleJpaRepository extends JpaRepository<RoleJpaEntity, Long> {

    Optional<RoleJpaEntity> findByUuid(UUID uuid);

    Optional<RoleJpaEntity> findByName(String name);

    List<RoleJpaEntity> findAllByUuidIn(Collection<UUID> uuids);

    boolean existsByName(String name);

    @Query("""
        SELECT r FROM RoleJpaEntity r 
        WHERE (:name IS NULL OR :name = '' OR LOWER(r.name) LIKE LOWER(CONCAT('%', :name, '%')))
          AND (:isActive IS NULL OR r.isActive = :isActive)
          AND r.isDeleted = false
    """)
    Page<RoleJpaEntity> findAllRoles(
            @Param("name") String name,
            @Param("isActive") Boolean isActive,
            Pageable pageable
    );
}

