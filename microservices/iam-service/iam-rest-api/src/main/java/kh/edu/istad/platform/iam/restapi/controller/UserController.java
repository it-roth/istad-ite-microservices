package kh.edu.istad.platform.iam.restapi.controller;

import jakarta.validation.Valid;
import kh.edu.istad.platform.iam.domain.dto.AssignRolesToUserCommand;
import kh.edu.istad.platform.iam.domain.dto.CreateUserCommand;
import kh.edu.istad.platform.iam.domain.dto.GrantPermissionsToUserCommand;
import kh.edu.istad.platform.iam.domain.dto.IdentityResult;
import kh.edu.istad.platform.iam.domain.dto.PageResult;
import kh.edu.istad.platform.iam.domain.dto.PermissionGrantResult;
import kh.edu.istad.platform.iam.domain.dto.ResetUserPasswordCommand;
import kh.edu.istad.platform.iam.domain.dto.RoleAssignmentResult;
import kh.edu.istad.platform.iam.domain.dto.UpdateUserProfileCommand;
import kh.edu.istad.platform.iam.domain.dto.UserAuthoritiesResult;
import kh.edu.istad.platform.iam.domain.dto.UserFilterQuery;
import kh.edu.istad.platform.iam.domain.dto.UserResult;
import kh.edu.istad.platform.iam.domain.dto.UserSummary;
import kh.edu.istad.platform.iam.domain.usecase.AssignRolesToUserUseCase;
import kh.edu.istad.platform.iam.domain.usecase.CreateUserUseCase;
import kh.edu.istad.platform.iam.domain.usecase.DeleteUserUseCase;
import kh.edu.istad.platform.iam.domain.usecase.DisableUserUseCase;
import kh.edu.istad.platform.iam.domain.usecase.EnableUserUseCase;
import kh.edu.istad.platform.iam.domain.usecase.GetUserAuthoritiesUseCase;
import kh.edu.istad.platform.iam.domain.usecase.GetUserUseCase;
import kh.edu.istad.platform.iam.domain.usecase.GrantPermissionsToUserUseCase;
import kh.edu.istad.platform.iam.domain.usecase.ListUserIdentitiesUseCase;
import kh.edu.istad.platform.iam.domain.usecase.ListUsersUseCase;
import kh.edu.istad.platform.iam.domain.usecase.LockUserUseCase;
import kh.edu.istad.platform.iam.domain.usecase.ResetUserPasswordUseCase;
import kh.edu.istad.platform.iam.domain.usecase.RevokePermissionFromUserUseCase;
import kh.edu.istad.platform.iam.domain.usecase.RevokeRoleFromUserUseCase;
import kh.edu.istad.platform.iam.domain.usecase.UnlockUserUseCase;
import kh.edu.istad.platform.iam.domain.usecase.UnlinkIdentityUseCase;
import kh.edu.istad.platform.iam.domain.usecase.UpdateUserProfileUseCase;
import kh.edu.istad.platform.iam.restapi.dto.AssignRolesToUserRequest;
import kh.edu.istad.platform.iam.restapi.dto.CreateUserRequest;
import kh.edu.istad.platform.iam.restapi.dto.GrantPermissionsToUserRequest;
import kh.edu.istad.platform.iam.restapi.dto.IdentityResponse;
import kh.edu.istad.platform.iam.restapi.dto.PageResponse;
import kh.edu.istad.platform.iam.restapi.dto.ResetPasswordRequest;
import kh.edu.istad.platform.iam.restapi.dto.UpdateUserProfileRequest;
import kh.edu.istad.platform.iam.restapi.dto.UserAuthoritiesResponse;
import kh.edu.istad.platform.iam.restapi.dto.UserResponse;
import kh.edu.istad.platform.iam.restapi.dto.UserRoleSummaryResponse;
import kh.edu.istad.platform.iam.restapi.mapper.IamWebMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final CreateUserUseCase createUserUseCase;
    private final ListUsersUseCase listUsersUseCase;
    private final GetUserUseCase getUserUseCase;
    private final UpdateUserProfileUseCase updateUserProfileUseCase;
    private final DeleteUserUseCase deleteUserUseCase;
    private final EnableUserUseCase enableUserUseCase;
    private final DisableUserUseCase disableUserUseCase;
    private final LockUserUseCase lockUserUseCase;
    private final UnlockUserUseCase unlockUserUseCase;
    private final ResetUserPasswordUseCase resetUserPasswordUseCase;
    private final GetUserAuthoritiesUseCase getUserAuthoritiesUseCase;
    private final AssignRolesToUserUseCase assignRolesToUserUseCase;
    private final RevokeRoleFromUserUseCase revokeRoleFromUserUseCase;
    private final GrantPermissionsToUserUseCase grantPermissionsToUserUseCase;
    private final RevokePermissionFromUserUseCase revokePermissionFromUserUseCase;
    private final ListUserIdentitiesUseCase listUserIdentitiesUseCase;
    private final UnlinkIdentityUseCase unlinkIdentityUseCase;
    private final IamWebMapper iamWebMapper;

    @PostMapping
    @PreAuthorize("hasAuthority('user:create')")
    public ResponseEntity<UserResponse> createUser(
            @Valid @RequestBody CreateUserRequest request
    ) {
        CreateUserCommand command = iamWebMapper.toCommand(request);
        UserResult userResult = createUserUseCase.execute(command);
        UserAuthoritiesResult authoritiesResult = getUserAuthoritiesUseCase.execute(userResult.uuid());

        List<UserRoleSummaryResponse> roleSummaries = List.of();
        if (authoritiesResult.roles() != null) {
            roleSummaries = authoritiesResult.roles().stream()
                    .map(r -> {
                        return new UserRoleSummaryResponse(r.uuid(), r.name());
                    })
                    .toList();
        }

        UserResponse response = iamWebMapper.toResponse(userResult, roleSummaries);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('user:read')")
    public ResponseEntity<PageResponse<UserSummary>> listUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String query,
            @RequestParam(required = false) Boolean enabled
    ) {
        UserFilterQuery filterQuery = new UserFilterQuery(query, enabled, page, size);
        PageResult<UserSummary> result = listUsersUseCase.execute(filterQuery);
        PageResponse<UserSummary> response = iamWebMapper.toPageResponse(result, item -> {
            return item;
        });
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{uuid}")
    @PreAuthorize("hasAuthority('user:read')")
    public ResponseEntity<UserResponse> getUser(
            @PathVariable UUID uuid
    ) {
        UserResult userResult = getUserUseCase.execute(uuid);
        UserAuthoritiesResult authoritiesResult = getUserAuthoritiesUseCase.execute(uuid);

        List<UserRoleSummaryResponse> roleSummaries = List.of();
        if (authoritiesResult.roles() != null) {
            roleSummaries = authoritiesResult.roles().stream()
                    .map(r -> {
                        return new UserRoleSummaryResponse(r.uuid(), r.name());
                    })
                    .toList();
        }

        UserResponse response = iamWebMapper.toResponse(userResult, roleSummaries);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{uuid}")
    @PreAuthorize("hasAuthority('user:update')")
    public ResponseEntity<UserResponse> updateUserProfile(
            @PathVariable UUID uuid,
            @Valid @RequestBody UpdateUserProfileRequest request
    ) {
        UpdateUserProfileCommand command = iamWebMapper.toCommand(uuid, request);
        UserResult userResult = updateUserProfileUseCase.execute(command);
        UserAuthoritiesResult authoritiesResult = getUserAuthoritiesUseCase.execute(uuid);

        List<UserRoleSummaryResponse> roleSummaries = List.of();
        if (authoritiesResult.roles() != null) {
            roleSummaries = authoritiesResult.roles().stream()
                    .map(r -> {
                        return new UserRoleSummaryResponse(r.uuid(), r.name());
                    })
                    .toList();
        }

        UserResponse response = iamWebMapper.toResponse(userResult, roleSummaries);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{uuid}")
    @PreAuthorize("hasAuthority('user:delete')")
    public ResponseEntity<Void> deleteUser(
            @PathVariable UUID uuid
    ) {
        deleteUserUseCase.execute(uuid);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{uuid}/enable")
    @PreAuthorize("hasAuthority('user:update')")
    public ResponseEntity<Void> enableUser(
            @PathVariable UUID uuid
    ) {
        enableUserUseCase.execute(uuid);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{uuid}/disable")
    @PreAuthorize("hasAuthority('user:update')")
    public ResponseEntity<Void> disableUser(
            @PathVariable UUID uuid
    ) {
        disableUserUseCase.execute(uuid);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{uuid}/lock")
    @PreAuthorize("hasAuthority('user:update')")
    public ResponseEntity<Void> lockUser(
            @PathVariable UUID uuid
    ) {
        lockUserUseCase.execute(uuid);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{uuid}/unlock")
    @PreAuthorize("hasAuthority('user:update')")
    public ResponseEntity<Void> unlockUser(
            @PathVariable UUID uuid
    ) {
        unlockUserUseCase.execute(uuid);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{uuid}/password")
    @PreAuthorize("hasAuthority('user:update')")
    public ResponseEntity<Void> resetPassword(
            @PathVariable UUID uuid,
            @Valid @RequestBody ResetPasswordRequest request
    ) {
        if (!request.newPassword().equals(request.confirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }
        ResetUserPasswordCommand command = new ResetUserPasswordCommand(uuid, request.newPassword());
        resetUserPasswordUseCase.execute(command);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{uuid}/authorities")
    @PreAuthorize("hasAuthority('user:read')")
    public ResponseEntity<UserAuthoritiesResponse> getUserAuthorities(
            @PathVariable UUID uuid
    ) {
        UserAuthoritiesResult result = getUserAuthoritiesUseCase.execute(uuid);
        UserAuthoritiesResponse response = iamWebMapper.toResponse(result);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{uuid}/roles")
    @PreAuthorize("hasAuthority('user:assign')")
    public ResponseEntity<List<RoleAssignmentResult>> assignRoles(
            @PathVariable UUID uuid,
            @Valid @RequestBody AssignRolesToUserRequest request
    ) {
        AssignRolesToUserCommand command = iamWebMapper.toCommand(uuid, request);
        List<RoleAssignmentResult> result = assignRolesToUserUseCase.execute(command);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{uuid}/roles/{roleUuid}")
    @PreAuthorize("hasAuthority('user:assign')")
    public ResponseEntity<Void> revokeRole(
            @PathVariable UUID uuid,
            @PathVariable UUID roleUuid,
            @RequestParam(required = false, defaultValue = "Revoked by admin") String reason
    ) {
        revokeRoleFromUserUseCase.execute(uuid, roleUuid, reason);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{uuid}/permissions")
    @PreAuthorize("hasAuthority('user:assign')")
    public ResponseEntity<List<PermissionGrantResult>> grantPermissions(
            @PathVariable UUID uuid,
            @Valid @RequestBody GrantPermissionsToUserRequest request
    ) {
        GrantPermissionsToUserCommand command = iamWebMapper.toCommand(uuid, request);
        List<PermissionGrantResult> result = grantPermissionsToUserUseCase.execute(command);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{uuid}/permissions/{permissionUuid}")
    @PreAuthorize("hasAuthority('user:assign')")
    public ResponseEntity<Void> revokePermission(
            @PathVariable UUID uuid,
            @PathVariable UUID permissionUuid,
            @RequestParam(required = false, defaultValue = "Revoked by admin") String reason
    ) {
        revokePermissionFromUserUseCase.execute(uuid, permissionUuid, reason);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{uuid}/identities")
    @PreAuthorize("hasAuthority('user:read')")
    public ResponseEntity<List<IdentityResponse>> listIdentities(
            @PathVariable UUID uuid
    ) {
        List<IdentityResult> results = listUserIdentitiesUseCase.execute(uuid);
        List<IdentityResponse> responses = results.stream()
                .map(iamWebMapper::toResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @DeleteMapping("/{uuid}/identities/{identityUuid}")
    @PreAuthorize("hasAuthority('user:update')")
    public ResponseEntity<Void> unlinkIdentity(
            @PathVariable UUID uuid,
            @PathVariable UUID identityUuid
    ) {
        unlinkIdentityUseCase.execute(uuid, identityUuid);
        return ResponseEntity.noContent().build();
    }
}
