package kh.edu.istad.platform.iam.restapi.controller;

import jakarta.validation.Valid;
import kh.edu.istad.platform.iam.domain.dto.AuthUserResult;
import kh.edu.istad.platform.iam.domain.dto.ChangePasswordCommand;
import kh.edu.istad.platform.iam.domain.dto.IdentityResult;
import kh.edu.istad.platform.iam.domain.dto.UpdateUserProfileCommand;
import kh.edu.istad.platform.iam.domain.dto.UserAuthoritiesResult;
import kh.edu.istad.platform.iam.domain.dto.UserResult;
import kh.edu.istad.platform.iam.domain.usecase.ChangePasswordUseCase;
import kh.edu.istad.platform.iam.domain.usecase.GetUserAuthoritiesUseCase;
import kh.edu.istad.platform.iam.domain.usecase.GetUserUseCase;
import kh.edu.istad.platform.iam.domain.usecase.ListUserIdentitiesUseCase;
import kh.edu.istad.platform.iam.domain.usecase.LoadUserForAuthenticationUseCase;
import kh.edu.istad.platform.iam.domain.usecase.UnlinkIdentityUseCase;
import kh.edu.istad.platform.iam.domain.usecase.UpdateUserProfileUseCase;
import kh.edu.istad.platform.iam.restapi.dto.ChangePasswordRequest;
import kh.edu.istad.platform.iam.restapi.dto.IdentityResponse;
import kh.edu.istad.platform.iam.restapi.dto.UpdateUserProfileRequest;
import kh.edu.istad.platform.iam.restapi.dto.UserResponse;
import kh.edu.istad.platform.iam.restapi.dto.UserRoleSummaryResponse;
import kh.edu.istad.platform.iam.restapi.mapper.IamWebMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/me")
@RequiredArgsConstructor
public class MeController {

    private final GetUserUseCase getUserUseCase;
    private final UpdateUserProfileUseCase updateUserProfileUseCase;
    private final ChangePasswordUseCase changePasswordUseCase;
    private final ListUserIdentitiesUseCase listUserIdentitiesUseCase;
    private final UnlinkIdentityUseCase unlinkIdentityUseCase;
    private final GetUserAuthoritiesUseCase getUserAuthoritiesUseCase;
    private final LoadUserForAuthenticationUseCase loadUserForAuthenticationUseCase;
    private final IamWebMapper iamWebMapper;

    @GetMapping
    public ResponseEntity<UserResponse> getMe(
            Authentication authentication
    ) {
        UUID userUuid = resolveCurrentUserUuid(authentication);
        UserResult userResult = getUserUseCase.execute(userUuid);
        UserAuthoritiesResult authoritiesResult = getUserAuthoritiesUseCase.execute(userUuid);

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

    @PatchMapping
    public ResponseEntity<UserResponse> updateMe(
            @Valid @RequestBody UpdateUserProfileRequest request,
            Authentication authentication
    ) {
        UUID userUuid = resolveCurrentUserUuid(authentication);
        UpdateUserProfileCommand command = iamWebMapper.toCommand(userUuid, request);
        UserResult userResult = updateUserProfileUseCase.execute(command);
        UserAuthoritiesResult authoritiesResult = getUserAuthoritiesUseCase.execute(userUuid);

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

    @PutMapping("/password")
    public ResponseEntity<Void> changeMyPassword(
            @Valid @RequestBody ChangePasswordRequest request,
            Authentication authentication
    ) {
        if (!request.newPassword().equals(request.confirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }
        UUID userUuid = resolveCurrentUserUuid(authentication);
        ChangePasswordCommand command = iamWebMapper.toCommand(userUuid, request);
        changePasswordUseCase.execute(command);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/identities")
    public ResponseEntity<List<IdentityResponse>> getMyIdentities(
            Authentication authentication
    ) {
        UUID userUuid = resolveCurrentUserUuid(authentication);
        List<IdentityResult> results = listUserIdentitiesUseCase.execute(userUuid);
        List<IdentityResponse> responses = results.stream()
                .map(iamWebMapper::toResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @DeleteMapping("/identities/{identityUuid}")
    public ResponseEntity<Void> unlinkMyIdentity(
            @PathVariable UUID identityUuid,
            Authentication authentication
    ) {
        UUID userUuid = resolveCurrentUserUuid(authentication);
        unlinkIdentityUseCase.execute(userUuid, identityUuid);
        return ResponseEntity.noContent().build();
    }

    private UUID resolveCurrentUserUuid(
            Authentication authentication
    ) {
        if (authentication == null || authentication.getName() == null) {
            throw new AccessDeniedException("User is not authenticated");
        }
        String name = authentication.getName();
        try {
            return UUID.fromString(name);
        } catch (IllegalArgumentException ex) {
            AuthUserResult authUser = loadUserForAuthenticationUseCase.execute(name);
            return authUser.uuid();
        }
    }
}
