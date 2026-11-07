package kh.edu.istad.platform.iam.persistence.adapter;

import kh.edu.istad.platform.iam.domain.dto.PageResult;
import kh.edu.istad.platform.iam.domain.dto.PermissionFilterQuery;
import kh.edu.istad.platform.iam.domain.dto.PermissionResult;
import kh.edu.istad.platform.iam.domain.entity.Permission;
import kh.edu.istad.platform.iam.domain.port.out.PermissionRepository;
import kh.edu.istad.platform.iam.persistence.entity.PermissionJpaEntity;
import kh.edu.istad.platform.iam.persistence.mapper.PermissionPersistenceMapper;
import kh.edu.istad.platform.iam.persistence.repository.PermissionJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class PermissionRepositoryAdapter implements PermissionRepository {

    private final PermissionJpaRepository permissionJpaRepository;
    private final PermissionPersistenceMapper mapper;

    @Override
    public Permission save(Permission permission) {
        PermissionJpaEntity entity = mapper.toEntity(permission);
        return mapper.toDomain(permissionJpaRepository.save(entity));
    }

    @Override
    public Optional<Permission> findById(Long id) {
        return permissionJpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Permission> findByUuid(UUID uuid) {
        return permissionJpaRepository.findByUuid(uuid).map(mapper::toDomain);
    }

    @Override
    public Optional<Permission> findByName(String name) {
        return permissionJpaRepository.findByName(name).map(mapper::toDomain);
    }

    @Override
    public List<Permission> findAllByIds(Collection<Long> ids) {
        return permissionJpaRepository.findAllById(ids).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<Permission> findAllByUuids(Collection<UUID> uuids) {
        return permissionJpaRepository.findAllByUuidIn(uuids).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public boolean existsByName(String name) {
        return permissionJpaRepository.existsByName(name);
    }

    @Override
    public PageResult<PermissionResult> findAll(PermissionFilterQuery query) {
        PageRequest pageRequest = PageRequest.of(query.page(), query.size(), Sort.by("name").ascending());
        Page<PermissionJpaEntity> page = permissionJpaRepository.findAllPermissions(query.resource(), query.action(), pageRequest);

        List<PermissionResult> list = page.getContent().stream()
                .map(mapper::toDomain)
                .map(p -> {
                    return new PermissionResult(
                            p.getId().value(),
                            p.getName(),
                            p.getResource(),
                            p.getAction(),
                            p.getDescription(),
                            p.getSystem(),
                            p.getActive(),
                            p.getCreatedAt()
                    );
                })
                .toList();

        return new PageResult<>(
                list,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast()
        );
    }
}

