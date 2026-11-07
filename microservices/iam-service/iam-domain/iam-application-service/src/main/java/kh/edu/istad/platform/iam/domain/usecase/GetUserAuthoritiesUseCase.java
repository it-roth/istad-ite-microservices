package kh.edu.istad.platform.iam.domain.usecase;

import kh.edu.istad.platform.iam.domain.dto.UserAuthoritiesResult;
import kh.edu.istad.platform.iam.domain.entity.*;
import kh.edu.istad.platform.iam.domain.exception.IamDomainException;
import kh.edu.istad.platform.iam.domain.port.out.PermissionRepository;
import kh.edu.istad.platform.iam.domain.port.out.RoleRepository;
import kh.edu.istad.platform.iam.domain.port.out.UserRepository;
import kh.edu.istad.platform.iam.domain.service.IamDomainService;
import kh.edu.istad.platform.iam.domain.valueobject.PermissionEffect;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
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
                .orElseThrow(() -> new IamDomainException("User not found: " + userUuid));

        // 1. Load active roles
        List<UserRoleAssignment> roleAssignments = userRepository.findActiveRoleAssignments(user.getInternalId());
        List<Long> roleIds = roleAssignments.stream().map(UserRoleAssignment::getRoleId).toList();
        List<Role> activeRoles = roleRepository.findAllByIds(roleIds).stream()
                .filter(r -> Boolean.TRUE.equals(r.getActive()) && !Boolean.TRUE.equals(r.getDeleted()))
                .toList();

        // 2. Load role permissions
        List<RolePermissionAssignment> rpa = roleRepository.findActiveRolePermissionAssignmentsByRoleIds(
                activeRoles.stream().map(Role::getInternalId).toList());
        List<Long> rolePermIds = rpa.stream().map(RolePermissionAssignment::getPermissionId).distinct().toList();
        Map<Long, Permission> allPermissions = permissionRepository.findAllByIds(rolePermIds).stream()
                .collect(Collectors.toMap(Permission::getInternalId, p -> p));

        Set<String> rolePermissionNames = rpa.stream()
                .map(a -> allPermissions.get(a.getPermissionId()))
                .filter(p -> p != null && Boolean.TRUE.equals(p.getActive()) && !Boolean.TRUE.equals(p.getDeleted()))
                .map(Permission::getName)
                .collect(Collectors.toSet());

        // 3. Load direct user permissions (GRANT / DENY)
        List<UserPermissionAssignment> upa = userRepository.findActivePermissionAssignments(user.getInternalId());
        List<Long> userPermIds = upa.stream().map(UserPermissionAssignment::getPermissionId).distinct().toList();
        Map<Long, Permission> directPermsMap = permissionRepository.findAllByIds(userPermIds).stream()
                .collect(Collectors.toMap(Permission::getInternalId, p -> p));

        Set<String> directGrant = new HashSet<>();
        Set<String> directDeny = new HashSet<>();
        for (UserPermissionAssignment a : upa) {
            Permission p = directPermsMap.get(a.getPermissionId());
            if (p != null && Boolean.TRUE.equals(p.getActive()) && !Boolean.TRUE.equals(p.getDeleted())) {
                if (a.getEffect() == PermissionEffect.GRANT) {
                    directGrant.add(p.getName());
                } else if (a.getEffect() == PermissionEffect.DENY) {
                    directDeny.add(p.getName());
                }
            }
        }

        // 4. Effective permissions = active role perms + direct GRANT - direct DENY
        Set<String> effectivePermissions = iamDomainService.calculateEffectivePermissions(rolePermissionNames,
                directGrant, directDeny);

        // 5. Build Spring Security authorities (ROLE_ + roleName and resource:action)
        Set<String> authorities = iamDomainService.buildAuthorities(activeRoles, effectivePermissions);
        Set<String> roleNames = activeRoles.stream().map(Role::getName).collect(Collectors.toSet());

        return new UserAuthoritiesResult(userUuid, roleNames, effectivePermissions, authorities);
    }
}
