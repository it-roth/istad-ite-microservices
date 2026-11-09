package kh.edu.istad.platform.iam.persistence.adapter;

import kh.edu.istad.platform.iam.domain.dto.PageResult;
import kh.edu.istad.platform.iam.domain.dto.RoleFilterQuery;
import kh.edu.istad.platform.iam.domain.dto.RoleSummary;
import kh.edu.istad.platform.iam.domain.entity.Role;
import kh.edu.istad.platform.iam.domain.entity.RolePermissionAssignment;
import kh.edu.istad.platform.iam.domain.port.out.RoleRepository;
import kh.edu.istad.platform.iam.persistence.entity.RoleJpaEntity;
import kh.edu.istad.platform.iam.persistence.entity.RolePermissionAssignmentJpaEntity;
import kh.edu.istad.platform.iam.persistence.mapper.RolePersistenceMapper;
import kh.edu.istad.platform.iam.persistence.repository.RoleJpaRepository;
import kh.edu.istad.platform.iam.persistence.repository.RolePermissionAssignmentJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class RoleRepositoryAdapter implements RoleRepository {

    private final RoleJpaRepository roleJpaRepository;
    private final RolePermissionAssignmentJpaRepository rolePermissionRepo;
    private final RolePersistenceMapper mapper;

    @Override
    public Role save(Role role) {
        RoleJpaEntity entity = mapper.toEntity(role);
        return mapper.toDomain(roleJpaRepository.save(entity));
    }

    @Override
    public Optional<Role> findById(Long id) {
        return roleJpaRepository.findByIdAndIsDeletedFalse(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Role> findByUuid(UUID uuid) {
        return roleJpaRepository.findByUuidAndIsDeletedFalse(uuid).map(mapper::toDomain);
    }

    @Override
    public Optional<Role> findByName(String name) {
        return roleJpaRepository.findByNameAndIsDeletedFalse(name).map(mapper::toDomain);
    }

    @Override
    public List<Role> findAllByIds(Collection<Long> ids) {
        return roleJpaRepository.findAllById(ids).stream()
                .filter(role -> Boolean.FALSE.equals(role.getIsDeleted()))
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<Role> findAllByUuids(Collection<UUID> uuids) {
        return roleJpaRepository.findAllByUuidInAndIsDeletedFalse(uuids).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public boolean existsByName(String name) {
        return roleJpaRepository.existsByName(name);
    }

    @Override
    public PageResult<RoleSummary> findAll(RoleFilterQuery query) {
        Sort sort = PageSort.parse(
                query.sort(),
                Sort.by("name").ascending(),
                Set.of("name", "isActive")
        );
        PageRequest pageRequest = PageRequest.of(query.page(), query.size(), sort);
        Page<RoleJpaEntity> page = roleJpaRepository.findAllRoles(query.name(), query.isActive(), pageRequest);

        List<RoleSummary> list = page.getContent().stream()
                .map(e -> {
                    return new RoleSummary(
                            e.getUuid(),
                            e.getName(),
                            e.getIsSystem(),
                            e.getIsActive()
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

    @Override
    public List<RolePermissionAssignment> findActiveRolePermissionAssignments(Long roleId) {
        return rolePermissionRepo.findActiveByRoleId(roleId, LocalDateTime.now()).stream()
                .map(e -> {
                    return RolePermissionAssignment.builder()
                            .id(e.getId())
                            .internalId(e.getId())
                            .roleId(e.getRoleId())
                            .permissionId(e.getPermissionId())
                            .assignedAt(e.getAssignedAt())
                            .assignedBy(e.getAssignedBy())
                            .expiresAt(e.getExpiresAt())
                            .revokedAt(e.getRevokedAt())
                            .revokedBy(e.getRevokedBy())
                            .reason(e.getReason())
                            .version(e.getVersion())
                            .build();
                })
                .toList();
    }

    @Override
    public List<RolePermissionAssignment> findActiveRolePermissionAssignmentsByRoleIds(Collection<Long> roleIds) {
        return rolePermissionRepo.findActiveByRoleIds(roleIds, LocalDateTime.now()).stream()
                .map(e -> {
                    return RolePermissionAssignment.builder()
                            .id(e.getId())
                            .internalId(e.getId())
                            .roleId(e.getRoleId())
                            .permissionId(e.getPermissionId())
                            .assignedAt(e.getAssignedAt())
                            .assignedBy(e.getAssignedBy())
                            .expiresAt(e.getExpiresAt())
                            .revokedAt(e.getRevokedAt())
                            .revokedBy(e.getRevokedBy())
                            .reason(e.getReason())
                            .version(e.getVersion())
                            .build();
                })
                .toList();
    }

    @Override
    public void savePermissionAssignments(List<RolePermissionAssignment> assignments) {
        List<RolePermissionAssignmentJpaEntity> entities = assignments.stream()
                .map(a -> {
                    return RolePermissionAssignmentJpaEntity.builder()
                            .id(a.getInternalId())
                            .roleId(a.getRoleId())
                            .permissionId(a.getPermissionId())
                            .assignedAt(a.getAssignedAt())
                            .assignedBy(a.getAssignedBy())
                            .expiresAt(a.getExpiresAt())
                            .revokedAt(a.getRevokedAt())
                            .revokedBy(a.getRevokedBy())
                            .reason(a.getReason())
                            .version(a.getVersion())
                            .build();
                })
                .toList();
        rolePermissionRepo.saveAll(entities);
    }
}
