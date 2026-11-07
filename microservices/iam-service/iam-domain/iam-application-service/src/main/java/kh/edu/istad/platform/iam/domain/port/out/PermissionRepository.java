package kh.edu.istad.platform.iam.domain.port.out;

import kh.edu.istad.platform.iam.domain.dto.PageResult;
import kh.edu.istad.platform.iam.domain.dto.PermissionFilterQuery;
import kh.edu.istad.platform.iam.domain.dto.PermissionResult;
import kh.edu.istad.platform.iam.domain.entity.Permission;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PermissionRepository {
    Permission save(Permission permission);

    Optional<Permission> findById(Long id);

    Optional<Permission> findByUuid(UUID uuid);

    Optional<Permission> findByName(String name);

    List<Permission> findAllByIds(Collection<Long> ids);

    List<Permission> findAllByUuids(Collection<UUID> uuids);

    boolean existsByName(String name);

    PageResult<PermissionResult> findAll(PermissionFilterQuery query);
}
