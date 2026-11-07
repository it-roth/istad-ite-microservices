package kh.edu.istad.platform.iam.domain.usecase;

import kh.edu.istad.platform.iam.domain.dto.UserAuthoritiesResult;
import kh.edu.istad.platform.iam.domain.entity.Permission;
import kh.edu.istad.platform.iam.domain.entity.Role;
import kh.edu.istad.platform.iam.domain.entity.RolePermissionAssignment;
import kh.edu.istad.platform.iam.domain.entity.User;
import kh.edu.istad.platform.iam.domain.entity.UserPermissionAssignment;
import kh.edu.istad.platform.iam.domain.entity.UserRoleAssignment;
import kh.edu.istad.platform.iam.domain.exception.IamDomainException;
import kh.edu.istad.platform.iam.domain.port.out.PermissionRepository;
import kh.edu.istad.platform.iam.domain.port.out.RoleRepository;
import kh.edu.istad.platform.iam.domain.port.out.UserRepository;
import kh.edu.istad.platform.iam.domain.service.IamDomainService;
import kh.edu.istad.platform.iam.domain.valueobject.PermissionEffect;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class GetUserAuthoritiesUseCase {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final IamDomainService iamDomainService;

    public UserAuthoritiesResult execute(UUID userUuid) {
        log.info("Calculating authorities for user UUID: {}", userUuid);

        User user = userRepository.findByUuid(userUuid)
                .orElseThrow(() -> {
                    return new IamDomainException("User not found: " + userUuid);
                });

        // 1. Load active roles
        List<UserRoleAssignment> roleAssignments = userRepository.findActiveRoleAssignments(user.getInternalId());
        List<Long> roleIds = roleAssignments.stream()
                .map(UserRoleAssignment::getRoleId)
                .toList();

        List<Role> activeRoles = roleRepository.findAllByIds(roleIds).stream()
                .filter(r -> {
                    return Boolean.TRUE.equals(r.getActive()) && !Boolean.TRUE.equals(r.getDeleted());
                })
                .toList();

        Map<Long, UserRoleAssignment> assignmentByRoleId = new HashMap<>();
        for (UserRoleAssignment assignment : roleAssignments) {
            assignmentByRoleId.put(assignment.getRoleId(), assignment);
        }

        List<UserAuthoritiesResult.RoleAuthorityItem> roleItems = new ArrayList<>();
        for (Role r : activeRoles) {
            UserRoleAssignment a = assignmentByRoleId.get(r.getInternalId());
            roleItems.add(new UserAuthoritiesResult.RoleAuthorityItem(
                    r.getId().value(),
                    r.getName(),
                    a != null ? a.getExpiresAt() : null
            ));
        }

        // 2. Load role permissions
        List<RolePermissionAssignment> rpa = roleRepository.findActiveRolePermissionAssignmentsByRoleIds(
                activeRoles.stream().map(Role::getInternalId).toList()
        );
        List<Long> rolePermIds = rpa.stream()
                .map(RolePermissionAssignment::getPermissionId)
                .distinct()
                .toList();

        Map<Long, Permission> allPermissions = permissionRepository.findAllByIds(rolePermIds).stream()
                .collect(Collectors.toMap(Permission::getInternalId, p -> {
                    return p;
                }));

        Set<String> rolePermissionNames = rpa.stream()
                .map(a -> {
                    return allPermissions.get(a.getPermissionId());
                })
                .filter(p -> {
                    return p != null && Boolean.TRUE.equals(p.getActive()) && !Boolean.TRUE.equals(p.getDeleted());
                })
                .map(Permission::getName)
                .collect(Collectors.toSet());

        // 3. Load direct user permissions (GRANT / DENY)
        List<UserPermissionAssignment> upa = userRepository.findActivePermissionAssignments(user.getInternalId());
        List<Long> userPermIds = upa.stream()
                .map(UserPermissionAssignment::getPermissionId)
                .distinct()
                .toList();

        Map<Long, Permission> directPermsMap = permissionRepository.findAllByIds(userPermIds).stream()
                .collect(Collectors.toMap(Permission::getInternalId, p -> {
                    return p;
                }));

        Set<String> directGrant = new HashSet<>();
        Set<String> directDeny = new HashSet<>();
        List<UserAuthoritiesResult.DirectPermissionItem> directPermItems = new ArrayList<>();

        for (UserPermissionAssignment a : upa) {
            Permission p = directPermsMap.get(a.getPermissionId());
            if (p != null && Boolean.TRUE.equals(p.getActive()) && !Boolean.TRUE.equals(p.getDeleted())) {
                directPermItems.add(new UserAuthoritiesResult.DirectPermissionItem(
                        p.getName(),
                        a.getEffect().name()
                ));
                if (a.getEffect() == PermissionEffect.GRANT) {
                    directGrant.add(p.getName());
                } else if (a.getEffect() == PermissionEffect.DENY) {
                    directDeny.add(p.getName());
                }
            }
        }

        // 4. Effective permissions = active role perms + direct GRANT - direct DENY
        Set<String> effectivePermissions = iamDomainService.calculateEffectivePermissions(
                rolePermissionNames,
                directGrant,
                directDeny
        );

        // 5. Build Spring Security authorities (ROLE_ + roleName and resource:action)
        Set<String> authorities = iamDomainService.buildAuthorities(activeRoles, effectivePermissions);

        return new UserAuthoritiesResult(
                userUuid,
                roleItems,
                directPermItems,
                effectivePermissions,
                authorities
        );
    }
}
