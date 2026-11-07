package kh.edu.istad.platform.iam.domain.service;

import kh.edu.istad.platform.iam.domain.entity.Identity;
import kh.edu.istad.platform.iam.domain.entity.Permission;
import kh.edu.istad.platform.iam.domain.entity.Role;
import kh.edu.istad.platform.iam.domain.entity.User;
import kh.edu.istad.platform.iam.domain.exception.IamDomainException;
import kh.edu.istad.platform.iam.domain.valueobject.SocialProvider;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class IamDomainServiceImpl implements IamDomainService {

    @Override
    public Set<String> buildAuthorities(Collection<Role> activeRoles, Collection<String> effectivePermissions) {
        Set<String> authorities = new LinkedHashSet<>();
        if (activeRoles != null) {
            for (Role role : activeRoles) {
                if (role != null && role.getName() != null && !role.getName().isBlank()) {
                    authorities.add("ROLE_" + role.getName().trim().toUpperCase());
                }
            }
        }
        if (effectivePermissions != null) {
            for (String perm : effectivePermissions) {
                if (perm != null && !perm.isBlank()) {
                    authorities.add(perm.trim());
                }
            }
        }
        return authorities;
    }

    @Override
    public Set<String> calculateEffectivePermissions(
            Collection<String> activeRolePermissions,
            Collection<String> directGrantPermissions,
            Collection<String> directDenyPermissions) {
        Set<String> effective = new HashSet<>();
        if (activeRolePermissions != null) {
            effective.addAll(activeRolePermissions);
        }
        if (directGrantPermissions != null) {
            effective.addAll(directGrantPermissions);
        }
        if (directDenyPermissions != null) {
            effective.removeAll(directDenyPermissions);
        }
        return effective;
    }

    @Override
    public void validateRoleCanBeModifiedOrDeleted(Role role) {
        if (role != null && Boolean.TRUE.equals(role.getSystem())) {
            throw new IamDomainException("System role '" + role.getName() + "' cannot be changed or deleted");
        }
    }

    @Override
    public void validatePermissionCanBeModifiedOrDeleted(Permission permission) {
        if (permission != null && Boolean.TRUE.equals(permission.getSystem())) {
            throw new IamDomainException("System permission '" + permission.getName() + "' cannot be changed or deleted");
        }
    }

    @Override
    public void validateRoleCanBeAssigned(Role role) {
        if (role == null || !Boolean.TRUE.equals(role.getActive()) || Boolean.TRUE.equals(role.getDeleted())) {
            throw new IamDomainException("Inactive or deleted role cannot be assigned");
        }
    }

    @Override
    public void validatePermissionCanBeAssigned(Permission permission) {
        if (permission == null || !Boolean.TRUE.equals(permission.getActive()) || Boolean.TRUE.equals(permission.getDeleted())) {
            throw new IamDomainException("Inactive or deleted permission cannot be assigned");
        }
    }

    @Override
    public void validateIdentityCanBeUnlinked(User user, int activeIdentitiesCount) {
        boolean hasPassword = user != null && user.getPasswordHash() != null && !user.getPasswordHash().isBlank();
        if (!hasPassword && activeIdentitiesCount <= 1) {
            throw new IamDomainException("User cannot unlink their last sign-in method");
        }
    }

    @Override
    public void validateProviderNotAlreadyLinked(List<Identity> existingIdentities, SocialProvider provider) {
        if (existingIdentities == null || provider == null) {
            return;
        }
        boolean exists = existingIdentities.stream()
                .filter(id -> !Boolean.TRUE.equals(id.getDeleted()))
                .anyMatch(id -> provider.equals(id.getProvider()));
        if (exists) {
            throw new IamDomainException("Provider " + provider + " is already linked to this user");
        }
    }
}

