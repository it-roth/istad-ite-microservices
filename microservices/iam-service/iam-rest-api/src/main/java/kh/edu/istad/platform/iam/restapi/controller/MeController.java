package kh.edu.istad.platform.iam.restapi.controller;

import jakarta.validation.Valid;
import kh.edu.istad.platform.iam.domain.dto.AuthUserResult;
import kh.edu.istad.platform.iam.domain.dto.ChangePasswordCommand;
import kh.edu.istad.platform.iam.domain.dto.IdentityResult;
import kh.edu.istad.platform.iam.domain.dto.UpdateUserProfileCommand;
import kh.edu.istad.platform.iam.domain.dto.UserAuthoritiesResult;
import kh.edu.istad.platform.iam.domain.dto.UserResult;
import kh.edu.istad.platform.iam.domain.dto.OAuthClientResult;
import kh.edu.istad.platform.iam.domain.dto.ResetUserPasswordCommand;
import kh.edu.istad.platform.iam.domain.usecase.ChangePasswordUseCase;
import kh.edu.istad.platform.iam.domain.usecase.ResetUserPasswordUseCase;
import kh.edu.istad.platform.iam.domain.usecase.DeleteUserUseCase;
import kh.edu.istad.platform.iam.domain.usecase.GetUserAuthoritiesUseCase;
import kh.edu.istad.platform.iam.domain.usecase.GetUserUseCase;
import kh.edu.istad.platform.iam.domain.usecase.ListUserIdentitiesUseCase;
import kh.edu.istad.platform.iam.domain.usecase.LoadUserForAuthenticationUseCase;
import kh.edu.istad.platform.iam.domain.usecase.UnlinkIdentityUseCase;
import kh.edu.istad.platform.iam.domain.usecase.UpdateUserProfileUseCase;
import kh.edu.istad.platform.iam.domain.usecase.ListConnectedOAuthClientsUseCase;
import kh.edu.istad.platform.iam.domain.usecase.RevokeOAuthClientConsentUseCase;
import kh.edu.istad.platform.iam.restapi.dto.ChangePasswordRequest;
import kh.edu.istad.platform.iam.restapi.dto.IdentityResponse;
import kh.edu.istad.platform.iam.restapi.dto.ResetPasswordRequest;
import kh.edu.istad.platform.iam.restapi.dto.UpdateUserProfileRequest;
import kh.edu.istad.platform.iam.restapi.dto.UserAuthoritiesResponse;
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
import org.springframework.web.bind.annotation.PostMapping;
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

    private final DeleteUserUseCase deleteUserUseCase;
    private final GetUserUseCase getUserUseCase;
    private final UpdateUserProfileUseCase updateUserProfileUseCase;
    private final ChangePasswordUseCase changePasswordUseCase;
    private final ResetUserPasswordUseCase resetUserPasswordUseCase;
    private final ListUserIdentitiesUseCase listUserIdentitiesUseCase;
    private final UnlinkIdentityUseCase unlinkIdentityUseCase;
    private final GetUserAuthoritiesUseCase getUserAuthoritiesUseCase;
    private final LoadUserForAuthenticationUseCase loadUserForAuthenticationUseCase;
    private final IamWebMapper iamWebMapper;
    private final ListConnectedOAuthClientsUseCase listConnectedOAuthClientsUseCase;
    private final RevokeOAuthClientConsentUseCase revokeOAuthClientConsentUseCase;
    private final kh.edu.istad.platform.iam.domain.port.out.AccountSecurityRepository accountSecurityRepository;
    private final kh.edu.istad.platform.iam.domain.port.out.UserRepository userRepository;
    private final kh.edu.istad.platform.iam.domain.port.out.EmailSenderPort emailSenderPort;

    @GetMapping
    public ResponseEntity<UserResponse> getMe(
            Authentication authentication
    ) {
        UUID userUuid = resolveCurrentUserUuid(authentication);
        UserResult userResult = getUserUseCase.execute(userUuid);
        if (userResult.lastLoginAt() == null) {
            accountSecurityRepository.touchLastLogin(userUuid);
            userResult = getUserUseCase.execute(userUuid);
        }
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
        try {
            accountSecurityRepository.recordActivity(userUuid, "PROFILE_UPDATE",
                    "Updated user personal profile details", authentication.getName(), "127.0.0.1");
        } catch (Exception ignored) {}
        return ResponseEntity.ok(response);
    }

    @GetMapping("/authorities")
    public ResponseEntity<UserAuthoritiesResponse> getMyAuthorities(Authentication authentication) {
        UUID userUuid = resolveCurrentUserUuid(authentication);
        UserAuthoritiesResult result = getUserAuthoritiesUseCase.execute(userUuid);
        return ResponseEntity.ok(iamWebMapper.toResponse(result));
    }

    @PatchMapping
    public ResponseEntity<UserResponse> updateMe(
            @Valid @RequestBody UpdateUserProfileRequest request,
            Authentication authentication,
            jakarta.servlet.http.HttpServletRequest httpRequest
    ) {
        UUID userUuid = resolveCurrentUserUuid(authentication);
        UpdateUserProfileCommand command = iamWebMapper.toCommand(userUuid, request);
        UserResult userResult = updateUserProfileUseCase.execute(command);
        UserAuthoritiesResult authoritiesResult = getUserAuthoritiesUseCase.execute(userUuid);

        // Synchronize SecurityContext and HTTP Session if username was updated
        if (userResult.username() != null && !userResult.username().equals(authentication.getName())) {
            org.springframework.security.authentication.UsernamePasswordAuthenticationToken newAuth =
                    new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                            userResult.username(),
                            authentication.getCredentials(),
                            authentication.getAuthorities()
                    );
            org.springframework.security.core.context.SecurityContext context =
                    org.springframework.security.core.context.SecurityContextHolder.getContext();
            context.setAuthentication(newAuth);

            try {
                jakarta.servlet.http.HttpSession session = httpRequest.getSession(false);
                if (session != null) {
                    session.setAttribute(
                            "SPRING_SECURITY_CONTEXT",
                            context
                    );
                }
            } catch (Exception ignored) {}
        }

        List<UserRoleSummaryResponse> roleSummaries = List.of();
        if (authoritiesResult.roles() != null) {
            roleSummaries = authoritiesResult.roles().stream()
                    .map(r -> new UserRoleSummaryResponse(r.uuid(), r.name()))
                    .toList();
        }

        UserResponse response = iamWebMapper.toResponse(userResult, roleSummaries);
        try {
            accountSecurityRepository.recordActivity(userUuid, "PROFILE_UPDATE",
                    "Updated user personal profile details", authentication.getName(), "127.0.0.1");
        } catch (Exception ignored) {}
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
        try {
            accountSecurityRepository.recordActivity(userUuid, "PASSWORD_UPDATE",
                    "Account password changed successfully via security console", authentication.getName(), "127.0.0.1");
        } catch (Exception ignored) {}
        return ResponseEntity.noContent().build();
    }

    private static final java.util.concurrent.ConcurrentHashMap<String, String> RESET_CODES = new java.util.concurrent.ConcurrentHashMap<>();

    @PostMapping("/password/reset")
    public ResponseEntity<Void> resetMyPassword(
            @Valid @RequestBody ResetPasswordRequest request,
            Authentication authentication
    ) {
        if (!request.newPassword().equals(request.confirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }
        UUID userUuid = resolveCurrentUserUuid(authentication);
        UserResult userResult = getUserUseCase.execute(userUuid);
        String email = userResult.email();

        if (email != null && !email.isBlank()) {
            String expected = RESET_CODES.get(email.trim().toLowerCase());
            if (expected != null) {
                if (request.code() == null || !expected.equals(request.code().trim())) {
                    throw new IllegalArgumentException("Invalid verification code from Gmail");
                }
                RESET_CODES.remove(email.trim().toLowerCase());
            }
        }

        ResetUserPasswordCommand command = new ResetUserPasswordCommand(userUuid, request.newPassword());
        resetUserPasswordUseCase.execute(command);
        try {
            accountSecurityRepository.recordActivity(userUuid, "PASSWORD_RESET",
                    "Account password reset with email verification code", authentication.getName(), "127.0.0.1");
        } catch (Exception ignored) {}
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/password/reset-request")
    public ResponseEntity<java.util.Map<String, String>> requestPasswordReset(
            Authentication authentication
    ) {
        UUID userUuid = resolveCurrentUserUuid(authentication);
        UserResult userResult = getUserUseCase.execute(userUuid);
        String email = userResult.email();
        String code = String.valueOf((int) (100000 + Math.random() * 900000));
        if (email != null && !email.isBlank()) {
            RESET_CODES.put(email.trim().toLowerCase(), code);
            try {
                emailSenderPort.sendVerificationOtp(email.trim(), code, "Password Reset");
            } catch (Exception ignored) {}
        }
        return ResponseEntity.ok(java.util.Map.of(
                "message", "Verification code sent to " + (email != null ? email : "your email"),
                "email", email != null ? email : ""
        ));
    }

    @PostMapping("/verify-email")
    public ResponseEntity<Void> verifyMyEmail(Authentication authentication) {
        UUID userUuid = resolveCurrentUserUuid(authentication);
        userRepository.findByUuid(userUuid).ifPresent(user -> {
            user.setEmailVerified(true);
            userRepository.save(user);
        });
        try {
            accountSecurityRepository.recordActivity(userUuid, "PROFILE_UPDATE",
                    "Email address verified successfully", authentication.getName(), "127.0.0.1");
        } catch (Exception ignored) {}
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/two-factor")
    public ResponseEntity<java.util.Map<String, Object>> updateTwoFactor(
            @RequestBody java.util.Map<String, Object> body,
            Authentication authentication
    ) {
        boolean enabled = Boolean.TRUE.equals(body.get("enabled"));
        UUID userUuid = resolveCurrentUserUuid(authentication);
        userRepository.findByUuid(userUuid).ifPresent(user -> {
            user.setTwoFactorEnabled(enabled);
            userRepository.save(user);
        });
        try {
            accountSecurityRepository.recordActivity(userUuid, "SECURITY",
                    enabled ? "Enabled 2-Step Verification" : "Disabled 2-Step Verification",
                    authentication.getName(), "127.0.0.1");
        } catch (Exception ignored) {}
        return ResponseEntity.ok(java.util.Map.of("twoFactorEnabled", enabled));
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
            @PathVariable("identityUuid") UUID identityUuid,
            Authentication authentication
    ) {
        UUID userUuid = resolveCurrentUserUuid(authentication);
        unlinkIdentityUseCase.execute(userUuid, identityUuid);
        try {
            accountSecurityRepository.recordActivity(userUuid, "IDENTITY_UNLINK",
                    "Unlinked social identity provider", authentication.getName(), "127.0.0.1");
        } catch (Exception ignored) {}
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/connected-applications")
    public ResponseEntity<List<OAuthClientResult>> getConnectedApplications(Authentication authentication) {
        return ResponseEntity.ok(listConnectedOAuthClientsUseCase.execute(authentication.getName()));
    }

    @DeleteMapping("/connected-applications/{registeredClientId}")
    public ResponseEntity<Void> revokeConnectedApplication(@PathVariable("registeredClientId") String registeredClientId,
                                                           Authentication authentication) {
        revokeOAuthClientConsentUseCase.execute(registeredClientId, authentication.getName());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/deactivate")
    public ResponseEntity<Void> deactivateMe(
            Authentication authentication,
            jakarta.servlet.http.HttpServletRequest request,
            jakarta.servlet.http.HttpServletResponse response
    ) {
        UUID userUuid = resolveCurrentUserUuid(authentication);
        deleteUserUseCase.execute(userUuid);

        try {
            accountSecurityRepository.recordActivity(userUuid, "ACCOUNT_DEACTIVATED",
                    "User self-deactivated account", authentication != null ? authentication.getName() : "self",
                    request.getRemoteAddr());
        } catch (Exception ignored) {}

        jakarta.servlet.http.HttpSession session = request.getSession(false);
        if (session != null) {
            try {
                session.invalidate();
            } catch (Exception ignored) {}
        }
        org.springframework.security.core.context.SecurityContextHolder.clearContext();

        jakarta.servlet.http.Cookie jsessionCookie = new jakarta.servlet.http.Cookie("JSESSIONID", "");
        jsessionCookie.setPath("/");
        jsessionCookie.setMaxAge(0);
        response.addCookie(jsessionCookie);

        jakarta.servlet.http.Cookie rmCookie = new jakarta.servlet.http.Cookie("remember-me", "");
        rmCookie.setPath("/");
        rmCookie.setMaxAge(0);
        response.addCookie(rmCookie);

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
            try {
                AuthUserResult authUser = loadUserForAuthenticationUseCase.execute(name);
                return authUser.uuid();
            } catch (Exception ex2) {
                return userRepository.findByUsername(name)
                        .or(() -> userRepository.findByEmail(name))
                        .map(u -> u.getId().value())
                        .orElseThrow(() -> new AccessDeniedException("User not found: " + name));
            }
        }
    }
}
