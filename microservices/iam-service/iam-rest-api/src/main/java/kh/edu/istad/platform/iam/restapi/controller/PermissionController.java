package kh.edu.istad.platform.iam.restapi.controller;

import jakarta.validation.Valid;
import kh.edu.istad.platform.iam.domain.dto.CreatePermissionCommand;
import kh.edu.istad.platform.iam.domain.dto.PageResult;
import kh.edu.istad.platform.iam.domain.dto.PermissionFilterQuery;
import kh.edu.istad.platform.iam.domain.dto.PermissionResult;
import kh.edu.istad.platform.iam.domain.dto.UpdatePermissionCommand;
import kh.edu.istad.platform.iam.domain.usecase.ActivatePermissionUseCase;
import kh.edu.istad.platform.iam.domain.usecase.CreatePermissionUseCase;
import kh.edu.istad.platform.iam.domain.usecase.DeactivatePermissionUseCase;
import kh.edu.istad.platform.iam.domain.usecase.DeletePermissionUseCase;
import kh.edu.istad.platform.iam.domain.usecase.GetPermissionUseCase;
import kh.edu.istad.platform.iam.domain.usecase.ListPermissionsUseCase;
import kh.edu.istad.platform.iam.domain.usecase.UpdatePermissionUseCase;
import kh.edu.istad.platform.iam.restapi.dto.CreatePermissionRequest;
import kh.edu.istad.platform.iam.restapi.dto.PageResponse;
import kh.edu.istad.platform.iam.restapi.dto.PermissionResponse;
import kh.edu.istad.platform.iam.restapi.dto.UpdatePermissionRequest;
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

import java.util.UUID;

@RestController
@RequestMapping("/api/permissions")
@RequiredArgsConstructor
public class PermissionController {

    private final CreatePermissionUseCase createPermissionUseCase;
    private final ListPermissionsUseCase listPermissionsUseCase;
    private final GetPermissionUseCase getPermissionUseCase;
    private final UpdatePermissionUseCase updatePermissionUseCase;
    private final DeletePermissionUseCase deletePermissionUseCase;
    private final ActivatePermissionUseCase activatePermissionUseCase;
    private final DeactivatePermissionUseCase deactivatePermissionUseCase;
    private final IamWebMapper iamWebMapper;

    @PostMapping
    @PreAuthorize("hasAuthority('permission:create')")
    public ResponseEntity<PermissionResponse> createPermission(
            @Valid @RequestBody CreatePermissionRequest request
    ) {
        CreatePermissionCommand command = iamWebMapper.toCommand(request);
        PermissionResult result = createPermissionUseCase.execute(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(iamWebMapper.toResponse(result));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('permission:read')")
    public ResponseEntity<PageResponse<PermissionResponse>> listPermissions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String resource,
            @RequestParam(required = false) String action
    ) {
        PermissionFilterQuery filterQuery = new PermissionFilterQuery(resource, action, page, size);
        PageResult<PermissionResult> result = listPermissionsUseCase.execute(filterQuery);
        PageResponse<PermissionResponse> response = iamWebMapper.toPageResponse(result, iamWebMapper::toResponse);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{uuid}")
    @PreAuthorize("hasAuthority('permission:read')")
    public ResponseEntity<PermissionResponse> getPermission(
            @PathVariable UUID uuid
    ) {
        PermissionResult result = getPermissionUseCase.execute(uuid);
        return ResponseEntity.ok(iamWebMapper.toResponse(result));
    }

    @PatchMapping("/{uuid}")
    @PreAuthorize("hasAuthority('permission:update')")
    public ResponseEntity<PermissionResponse> updatePermission(
            @PathVariable UUID uuid,
            @Valid @RequestBody UpdatePermissionRequest request
    ) {
        UpdatePermissionCommand command = iamWebMapper.toCommand(uuid, request);
        PermissionResult result = updatePermissionUseCase.execute(command);
        return ResponseEntity.ok(iamWebMapper.toResponse(result));
    }

    @DeleteMapping("/{uuid}")
    @PreAuthorize("hasAuthority('permission:delete')")
    public ResponseEntity<Void> deletePermission(
            @PathVariable UUID uuid
    ) {
        deletePermissionUseCase.execute(uuid);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{uuid}/activate")
    @PreAuthorize("hasAuthority('permission:update')")
    public ResponseEntity<Void> activatePermission(
            @PathVariable UUID uuid
    ) {
        activatePermissionUseCase.execute(uuid);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{uuid}/deactivate")
    @PreAuthorize("hasAuthority('permission:update')")
    public ResponseEntity<Void> deactivatePermission(
            @PathVariable UUID uuid
    ) {
        deactivatePermissionUseCase.execute(uuid);
        return ResponseEntity.noContent().build();
    }
}
