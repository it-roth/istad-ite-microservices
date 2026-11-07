package kh.edu.istad.platform.iam.persistence.repository;

import kh.edu.istad.platform.iam.persistence.entity.IdentityJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface IdentityJpaRepository extends JpaRepository<IdentityJpaEntity, Long> {

    Optional<IdentityJpaEntity> findByUuid(UUID uuid);

    Optional<IdentityJpaEntity> findByProviderAndProviderSubject(String provider, String providerSubject);

    List<IdentityJpaEntity> findAllByUserIdAndIsDeletedFalse(Long userId);
}

