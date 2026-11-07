package kh.edu.istad.platform.iam.persistence.adapter;

import kh.edu.istad.platform.iam.domain.dto.PageResult;
import kh.edu.istad.platform.iam.domain.dto.UserFilterQuery;
import kh.edu.istad.platform.iam.domain.dto.UserSummary;
import kh.edu.istad.platform.iam.domain.entity.Identity;
import kh.edu.istad.platform.iam.domain.entity.User;
import kh.edu.istad.platform.iam.domain.entity.UserPermissionAssignment;
import kh.edu.istad.platform.iam.domain.entity.UserRoleAssignment;
import kh.edu.istad.platform.iam.domain.port.out.UserRepository;
import kh.edu.istad.platform.iam.domain.valueobject.PermissionEffect;
import kh.edu.istad.platform.iam.domain.valueobject.SocialProvider;
import kh.edu.istad.platform.iam.persistence.entity.IdentityJpaEntity;
import kh.edu.istad.platform.iam.persistence.entity.UserJpaEntity;
import kh.edu.istad.platform.iam.persistence.entity.UserPermissionAssignmentJpaEntity;
import kh.edu.istad.platform.iam.persistence.entity.UserRoleAssignmentJpaEntity;
import kh.edu.istad.platform.iam.persistence.mapper.IdentityPersistenceMapper;
import kh.edu.istad.platform.iam.persistence.mapper.UserPersistenceMapper;
import kh.edu.istad.platform.iam.persistence.repository.IdentityJpaRepository;
import kh.edu.istad.platform.iam.persistence.repository.UserJpaRepository;
import kh.edu.istad.platform.iam.persistence.repository.UserPermissionAssignmentJpaRepository;
import kh.edu.istad.platform.iam.persistence.repository.UserRoleAssignmentJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepository {

    private final UserJpaRepository userJpaRepository;
    private final UserRoleAssignmentJpaRepository userRoleRepo;
    private final UserPermissionAssignmentJpaRepository userPermRepo;
    private final IdentityJpaRepository identityRepo;
    private final UserPersistenceMapper userMapper;
    private final IdentityPersistenceMapper identityMapper;

    @Override
    public User save(User user) {
        UserJpaEntity entity = userMapper.toEntity(user);
        UserJpaEntity saved = userJpaRepository.save(entity);
        return userMapper.toDomain(saved);
    }

    @Override
    public Optional<User> findById(Long id) {
        return userJpaRepository.findById(id).map(userMapper::toDomain);
    }

    @Override
    public Optional<User> findByUuid(UUID uuid) {
        return userJpaRepository.findByUuid(uuid).map(userMapper::toDomain);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userJpaRepository.findByUsername(username).map(userMapper::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userJpaRepository.findByEmail(email).map(userMapper::toDomain);
    }

    @Override
    public Optional<User> findByUsernameOrEmail(String usernameOrEmail) {
        return userJpaRepository.findByUsernameOrEmail(usernameOrEmail).map(userMapper::toDomain);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userJpaRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userJpaRepository.existsByEmail(email);
    }

    @Override
    public PageResult<UserSummary> findAll(UserFilterQuery query) {
        PageRequest pageRequest = PageRequest.of(query.page(), query.size(), Sort.by("createdAt").descending());
        Page<UserJpaEntity> page = userJpaRepository.findAllUsers(query.query(), query.enabled(), pageRequest);

        List<UserSummary> list = page.getContent().stream()
                .map(e -> {
                    return new UserSummary(
                            e.getUuid(),
                            e.getUsername(),
                            e.getEmail(),
                            e.getGivenName(),
                            e.getFamilyName(),
                            e.getEnabled(),
                            e.getCreatedAt()
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
    public List<UserRoleAssignment> findActiveRoleAssignments(Long userId) {
        return userRoleRepo.findActiveByUserId(userId, LocalDateTime.now()).stream()
                .map(e -> {
                    return UserRoleAssignment.builder()
                            .id(e.getId())
                            .internalId(e.getId())
                            .userId(e.getUserId())
                            .roleId(e.getRoleId())
                            .assignedAt(e.getAssignedAt())
                            .assignedBy(e.getAssignedBy())
                            .expiresAt(e.getExpiresAt())
                            .revokedAt(e.getRevokedAt())
                            .revokedBy(e.getRevokedBy())
                            .reason(e.getReason())
                            .build();
                })
                .toList();
    }

    @Override
    public List<UserPermissionAssignment> findActivePermissionAssignments(Long userId) {
        return userPermRepo.findActiveByUserId(userId, LocalDateTime.now()).stream()
                .map(e -> {
                    return UserPermissionAssignment.builder()
                            .id(e.getId())
                            .internalId(e.getId())
                            .userId(e.getUserId())
                            .permissionId(e.getPermissionId())
                            .effect(PermissionEffect.valueOf(e.getEffect()))
                            .assignedAt(e.getAssignedAt())
                            .assignedBy(e.getAssignedBy())
                            .expiresAt(e.getExpiresAt())
                            .revokedAt(e.getRevokedAt())
                            .revokedBy(e.getRevokedBy())
                            .reason(e.getReason())
                            .build();
                })
                .toList();
    }

    @Override
    public void saveRoleAssignments(List<UserRoleAssignment> assignments) {
        List<UserRoleAssignmentJpaEntity> entities = assignments.stream()
                .map(a -> {
                    return UserRoleAssignmentJpaEntity.builder()
                            .id(a.getInternalId())
                            .userId(a.getUserId())
                            .roleId(a.getRoleId())
                            .assignedAt(a.getAssignedAt())
                            .assignedBy(a.getAssignedBy())
                            .expiresAt(a.getExpiresAt())
                            .revokedAt(a.getRevokedAt())
                            .revokedBy(a.getRevokedBy())
                            .reason(a.getReason())
                            .build();
                })
                .toList();
        userRoleRepo.saveAll(entities);
    }

    @Override
    public void savePermissionAssignments(List<UserPermissionAssignment> assignments) {
        List<UserPermissionAssignmentJpaEntity> entities = assignments.stream()
                .map(a -> {
                    return UserPermissionAssignmentJpaEntity.builder()
                            .id(a.getInternalId())
                            .userId(a.getUserId())
                            .permissionId(a.getPermissionId())
                            .effect(a.getEffect().name())
                            .assignedAt(a.getAssignedAt())
                            .assignedBy(a.getAssignedBy())
                            .expiresAt(a.getExpiresAt())
                            .revokedAt(a.getRevokedAt())
                            .revokedBy(a.getRevokedBy())
                            .reason(a.getReason())
                            .build();
                })
                .toList();
        userPermRepo.saveAll(entities);
    }

    @Override
    public Optional<Identity> findIdentityByProviderAndSubject(SocialProvider provider, String providerSubject) {
        return identityRepo.findByProviderAndProviderSubject(provider.name(), providerSubject)
                .map(identityMapper::toDomain);
    }

    @Override
    public List<Identity> findIdentitiesByUserId(Long userId) {
        return identityRepo.findAllByUserIdAndIsDeletedFalse(userId).stream()
                .map(identityMapper::toDomain)
                .toList();
    }

    @Override
    public Identity saveIdentity(Identity identity) {
        IdentityJpaEntity entity = identityMapper.toEntity(identity);
        IdentityJpaEntity saved = identityRepo.save(entity);
        return identityMapper.toDomain(saved);
    }
}

