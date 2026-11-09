package kh.edu.istad.platform.iam.restapi.controller;

import jakarta.validation.Valid;
import kh.edu.istad.platform.iam.domain.dto.AssignPermissionsToRoleCommand;
import kh.edu.istad.platform.iam.domain.dto.CreateRoleCommand;
import kh.edu.istad.platform.iam.domain.dto.PageResult;
import kh.edu.istad.platform.iam.domain.dto.PermissionAssignmentResult;
import kh.edu.istad.platform.iam.domain.dto.RoleFilterQuery;
import kh.edu.istad.platform.iam.domain.dto.RoleResult;
import kh.edu.istad.platform.iam.domain.dto.RoleSummary;
import kh.edu.istad.platform.iam.domain.dto.UpdateRoleCommand;
import kh.edu.istad.platform.iam.domain.usecase.ActivateRoleUseCase;
import kh.edu.istad.platform.iam.domain.usecase.AssignPermissionsToRoleUseCase;
import kh.edu.istad.platform.iam.domain.usecase.CreateRoleUseCase;
import kh.edu.istad.platform.iam.domain.usecase.DeactivateRoleUseCase;
import kh.edu.istad.platform.iam.domain.usecase.DeleteRoleUseCase;
import kh.edu.istad.platform.iam.domain.usecase.GetRoleUseCase;
import kh.edu.istad.platform.iam.domain.usecase.ListRolesUseCase;
import kh.edu.istad.platform.iam.domain.usecase.RevokePermissionFromRoleUseCase;
import kh.edu.istad.platform.iam.domain.usecase.UpdateRoleUseCase;
import kh.edu.istad.platform.iam.restapi.dto.AssignPermissionsToRoleRequest;
import kh.edu.istad.platform.iam.restapi.dto.CreateRoleRequest;
import kh.edu.istad.platform.iam.restapi.dto.PageResponse;
import kh.edu.istad.platform.iam.restapi.dto.RoleResponse;
import kh.edu.istad.platform.iam.restapi.dto.UpdateRoleRequest;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {

    private final CreateRoleUseCase createRoleUseCase;
    private final ListRolesUseCase listRolesUseCase;
    private final GetRoleUseCase getRoleUseCase;
    private final UpdateRoleUseCase updateRoleUseCase;
    private final DeleteRoleUseCase deleteRoleUseCase;
    private final ActivateRoleUseCase activateRoleUseCase;
    private final DeactivateRoleUseCase deactivateRoleUseCase;
    private final AssignPermissionsToRoleUseCase assignPermissionsToRoleUseCase;
    private final RevokePermissionFromRoleUseCase revokePermissionFromRoleUseCase;
    private final IamWebMapper iamWebMapper;

    @PostMapping
    @PreAuthorize("hasAuthority('role:create')")
    public ResponseEntity<RoleResponse> createRole(
            @Valid @RequestBody CreateRoleRequest request
    ) {
        CreateRoleCommand command = iamWebMapper.toCommand(request);
        RoleResult result = createRoleUseCase.execute(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(iamWebMapper.toResponse(result));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('role:read')")
    public ResponseEntity<PageResponse<RoleResponse>> listRoles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String query,
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) String sort
    ) {
        RoleFilterQuery filterQuery = new RoleFilterQuery(query, active, page, size, sort);
        PageResult<RoleSummary> result = listRolesUseCase.execute(filterQuery);
        PageResponse<RoleResponse> response = iamWebMapper.toPageResponse(result, iamWebMapper::toResponse);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{uuid}")
    @PreAuthorize("hasAuthority('role:read')")
    public ResponseEntity<RoleResponse> getRole(
            @PathVariable UUID uuid
    ) {
        RoleResult result = getRoleUseCase.execute(uuid);
        return ResponseEntity.ok(iamWebMapper.toResponse(result));
    }

    @PatchMapping("/{uuid}")
    @PreAuthorize("hasAuthority('role:update')")
    public ResponseEntity<RoleResponse> updateRole(
            @PathVariable UUID uuid,
            @Valid @RequestBody UpdateRoleRequest request
    ) {
        UpdateRoleCommand command = iamWebMapper.toCommand(uuid, request);
        RoleResult result = updateRoleUseCase.execute(command);
        return ResponseEntity.ok(iamWebMapper.toResponse(result));
    }

    @DeleteMapping("/{uuid}")
    @PreAuthorize("hasAuthority('role:delete')")
    public ResponseEntity<Void> deleteRole(
            @PathVariable UUID uuid
    ) {
        deleteRoleUseCase.execute(uuid);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{uuid}/activate")
    @PreAuthorize("hasAuthority('role:update')")
    public ResponseEntity<Void> activateRole(
            @PathVariable UUID uuid
    ) {
        activateRoleUseCase.execute(uuid);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{uuid}/deactivate")
    @PreAuthorize("hasAuthority('role:update')")
    public ResponseEntity<Void> deactivateRole(
            @PathVariable UUID uuid
    ) {
        deactivateRoleUseCase.execute(uuid);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{uuid}/permissions")
    @PreAuthorize("hasAuthority('role:assign')")
    public ResponseEntity<List<PermissionAssignmentResult>> assignPermissions(
            @PathVariable UUID uuid,
            @Valid @RequestBody AssignPermissionsToRoleRequest request
    ) {
        AssignPermissionsToRoleCommand command = iamWebMapper.toCommand(uuid, request);
        List<PermissionAssignmentResult> result = assignPermissionsToRoleUseCase.execute(command);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{uuid}/permissions/{permissionUuid}")
    @PreAuthorize("hasAuthority('role:assign')")
    public ResponseEntity<Void> revokePermission(
            @PathVariable UUID uuid,
            @PathVariable UUID permissionUuid,
            @RequestParam(required = false, defaultValue = "Revoked by admin") String reason
    ) {
        revokePermissionFromRoleUseCase.execute(uuid, permissionUuid, reason);
        return ResponseEntity.noContent().build();
    }
}
