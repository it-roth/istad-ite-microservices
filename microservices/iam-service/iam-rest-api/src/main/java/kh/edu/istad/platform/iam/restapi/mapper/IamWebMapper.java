package kh.edu.istad.platform.iam.restapi.mapper;

import kh.edu.istad.platform.iam.domain.dto.AssignPermissionsToRoleCommand;
import kh.edu.istad.platform.iam.domain.dto.AssignRolesToUserCommand;
import kh.edu.istad.platform.iam.domain.dto.ChangePasswordCommand;
import kh.edu.istad.platform.iam.domain.dto.CreatePermissionCommand;
import kh.edu.istad.platform.iam.domain.dto.CreateRoleCommand;
import kh.edu.istad.platform.iam.domain.dto.CreateUserCommand;
import kh.edu.istad.platform.iam.domain.dto.GrantPermissionsToUserCommand;
import kh.edu.istad.platform.iam.domain.dto.IdentityResult;
import kh.edu.istad.platform.iam.domain.dto.PageResult;
import kh.edu.istad.platform.iam.domain.dto.PermissionResult;
import kh.edu.istad.platform.iam.domain.dto.RegisterUserCommand;
import kh.edu.istad.platform.iam.domain.dto.RoleResult;
import kh.edu.istad.platform.iam.domain.dto.RoleSummary;
import kh.edu.istad.platform.iam.domain.dto.UpdatePermissionCommand;
import kh.edu.istad.platform.iam.domain.dto.UpdateRoleCommand;
import kh.edu.istad.platform.iam.domain.dto.UpdateUserProfileCommand;
import kh.edu.istad.platform.iam.domain.dto.UserAuthoritiesResult;
import kh.edu.istad.platform.iam.domain.dto.UserResult;
import kh.edu.istad.platform.iam.restapi.dto.AddressDto;
import kh.edu.istad.platform.iam.restapi.dto.AssignPermissionsToRoleRequest;
import kh.edu.istad.platform.iam.restapi.dto.AssignRolesToUserRequest;
import kh.edu.istad.platform.iam.restapi.dto.ChangePasswordRequest;
import kh.edu.istad.platform.iam.restapi.dto.CreatePermissionRequest;
import kh.edu.istad.platform.iam.restapi.dto.CreateRoleRequest;
import kh.edu.istad.platform.iam.restapi.dto.CreateUserRequest;
import kh.edu.istad.platform.iam.restapi.dto.DirectPermissionResponse;
import kh.edu.istad.platform.iam.restapi.dto.GrantPermissionsToUserRequest;
import kh.edu.istad.platform.iam.restapi.dto.IdentityResponse;
import kh.edu.istad.platform.iam.restapi.dto.PageMetadata;
import kh.edu.istad.platform.iam.restapi.dto.PageResponse;
import kh.edu.istad.platform.iam.restapi.dto.PermissionResponse;
import kh.edu.istad.platform.iam.restapi.dto.RegisterUserRequest;
import kh.edu.istad.platform.iam.restapi.dto.RoleResponse;
import kh.edu.istad.platform.iam.restapi.dto.RoleWithExpiryResponse;
import kh.edu.istad.platform.iam.restapi.dto.UpdatePermissionRequest;
import kh.edu.istad.platform.iam.restapi.dto.UpdateRoleRequest;
import kh.edu.istad.platform.iam.restapi.dto.UpdateUserProfileRequest;
import kh.edu.istad.platform.iam.restapi.dto.UserAuthoritiesResponse;
import kh.edu.istad.platform.iam.restapi.dto.UserResponse;
import kh.edu.istad.platform.iam.restapi.dto.UserRoleSummaryResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

@Mapper(componentModel = "spring")
public interface IamWebMapper {

    @Mapping(target = "rawPassword", source = "password")
    RegisterUserCommand toCommand(RegisterUserRequest request);

    @Mapping(target = "rawPassword", source = "request.password")
    @Mapping(target = "streetAddress", source = "request.address.streetAddress")
    @Mapping(target = "locality", source = "request.address.locality")
    @Mapping(target = "region", source = "request.address.region")
    @Mapping(target = "postalCode", source = "request.address.postalCode")
    @Mapping(target = "country", source = "request.address.country")
    CreateUserCommand toCommand(CreateUserRequest request);

    @Mapping(target = "streetAddress", source = "request.address.streetAddress")
    @Mapping(target = "locality", source = "request.address.locality")
    @Mapping(target = "region", source = "request.address.region")
    @Mapping(target = "postalCode", source = "request.address.postalCode")
    @Mapping(target = "country", source = "request.address.country")
    UpdateUserProfileCommand toCommand(UUID userUuid, UpdateUserProfileRequest request);

    default ChangePasswordCommand toCommand(UUID userUuid, ChangePasswordRequest request) {
        return new ChangePasswordCommand(
                userUuid,
                request.currentPassword(),
                request.newPassword()
        );
    }

    default AssignRolesToUserCommand toCommand(UUID userUuid, AssignRolesToUserRequest request) {
        return new AssignRolesToUserCommand(
                userUuid,
                request.roleUuids(),
                request.expiresAt(),
                request.reason()
        );
    }

    default GrantPermissionsToUserCommand toCommand(UUID userUuid, GrantPermissionsToUserRequest request) {
        return new GrantPermissionsToUserCommand(
                userUuid,
                request.permissionUuids(),
                request.effect(),
                null,
                request.reason()
        );
    }

    CreateRoleCommand toCommand(CreateRoleRequest request);

    default UpdateRoleCommand toCommand(UUID roleUuid, UpdateRoleRequest request) {
        return new UpdateRoleCommand(
                roleUuid,
                request.description()
        );
    }

    default AssignPermissionsToRoleCommand toCommand(UUID roleUuid, AssignPermissionsToRoleRequest request) {
        return new AssignPermissionsToRoleCommand(
                roleUuid,
                request.permissionUuids(),
                request.expiresAt(),
                request.reason()
        );
    }

    CreatePermissionCommand toCommand(CreatePermissionRequest request);

    default UpdatePermissionCommand toCommand(UUID permissionUuid, UpdatePermissionRequest request) {
        return new UpdatePermissionCommand(
                permissionUuid,
                request.description()
        );
    }

    default UserResponse toResponse(UserResult user, List<UserRoleSummaryResponse> roles) {
        String fullName = ((user.givenName() != null ? user.givenName() : "") + " "
                + (user.familyName() != null ? user.familyName() : "")).trim();

        return new UserResponse(
                user.uuid(),
                user.username(),
                user.email(),
                user.emailVerified(),
                fullName,
                user.locale(),
                user.enabled(),
                roles != null ? roles : List.of(),
                user.createdAt(),
                user.updatedAt()
        );
    }

    default UserAuthoritiesResponse toResponse(UserAuthoritiesResult result) {
        List<RoleWithExpiryResponse> roles = new ArrayList<>();
        if (result.roles() != null) {
            for (UserAuthoritiesResult.RoleAuthorityItem r : result.roles()) {
                roles.add(new RoleWithExpiryResponse(
                        r.uuid(),
                        r.name(),
                        r.expiresAt()
                ));
            }
        }

        List<DirectPermissionResponse> directPermissions = new ArrayList<>();
        if (result.directPermissions() != null) {
            for (UserAuthoritiesResult.DirectPermissionItem d : result.directPermissions()) {
                directPermissions.add(new DirectPermissionResponse(
                        d.name(),
                        d.effect()
                ));
            }
        }

        List<String> effective = result.effectivePermissions() != null
                ? new ArrayList<>(result.effectivePermissions())
                : List.of();

        return new UserAuthoritiesResponse(
                roles,
                directPermissions,
                effective
        );
    }

    IdentityResponse toResponse(IdentityResult result);

    @Mapping(target = "active", source = "isActive")
    @Mapping(target = "system", source = "isSystem")
    RoleResponse toResponse(RoleResult result);

    @Mapping(target = "description", ignore = true)
    @Mapping(target = "active", source = "isActive")
    @Mapping(target = "system", source = "isSystem")
    RoleResponse toResponse(RoleSummary summary);

    @Mapping(target = "active", source = "isActive")
    @Mapping(target = "system", source = "isSystem")
    PermissionResponse toResponse(PermissionResult result);

    default <T, R> PageResponse<R> toPageResponse(PageResult<T> pageResult, Function<T, R> mapper) {
        List<R> content = pageResult.content().stream()
                .map(mapper)
                .toList();

        PageMetadata metadata = new PageMetadata(
                pageResult.pageNumber(),
                pageResult.pageSize(),
                pageResult.totalElements(),
                pageResult.totalPages()
        );

        return new PageResponse<>(
                content,
                metadata
        );
    }
}
