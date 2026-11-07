package kh.edu.istad.platform.iam.domain.service;

import kh.edu.istad.platform.iam.domain.entity.Identity;
import kh.edu.istad.platform.iam.domain.entity.Permission;
import kh.edu.istad.platform.iam.domain.entity.Role;
import kh.edu.istad.platform.iam.domain.entity.User;
import kh.edu.istad.platform.iam.domain.valueobject.SocialProvider;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Domain Service enforcing Section B.6 Domain rules:
 * - Authorities: ROLE_ + role name, and permission name (resource:action).
 * - Effective permissions = active role permissions + direct GRANT − direct DENY.
 * - System roles/permissions cannot be changed or deleted; inactive ones cannot be assigned.
 * - One identity per provider per user; a user cannot unlink their last sign-in method.
 */
public interface IamDomainService {

    Set<String> buildAuthorities(Collection<Role> activeRoles, Collection<String> effectivePermissions);

    Set<String> calculateEffectivePermissions(
            Collection<String> activeRolePermissions,
            Collection<String> directGrantPermissions,
            Collection<String> directDenyPermissions
    );

    void validateRoleCanBeModifiedOrDeleted(Role role);

    void validatePermissionCanBeModifiedOrDeleted(Permission permission);

    void validateRoleCanBeAssigned(Role role);

    void validatePermissionCanBeAssigned(Permission permission);

    void validateIdentityCanBeUnlinked(User user, int activeIdentitiesCount);

    void validateProviderNotAlreadyLinked(List<Identity> existingIdentities, SocialProvider provider);
}

