package kh.edu.istad.platform.iam.restapi.controller;

import jakarta.validation.Valid;
import kh.edu.istad.platform.iam.domain.dto.SecurityConfigurationResult;
import kh.edu.istad.platform.iam.domain.usecase.ManageSecurityConfigurationUseCase;
import kh.edu.istad.platform.iam.restapi.dto.SecurityConfigurationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/admin/security")
@RequiredArgsConstructor
public class SecurityConfigurationController {
    private final ManageSecurityConfigurationUseCase useCase;

    @GetMapping("/configuration")
    @PreAuthorize("hasAnyAuthority('security:read', 'ROLE_ADMIN')")
    public SecurityConfigurationResult get() { return useCase.get(); }

    @PutMapping("/configuration")
    @PreAuthorize("hasAnyAuthority('security:update', 'ROLE_ADMIN')")
    public SecurityConfigurationResult update(@Valid @RequestBody SecurityConfigurationRequest request) {
        return useCase.update(new SecurityConfigurationResult(request.mfaRequired(),
                request.maxFailedLoginAttempts(), request.sessionTimeoutMinutes(),
                request.passwordMinimumLength(), request.passwordRequireUppercase(),
                request.passwordRequireNumber(), request.passwordRequireSymbol()));
    }
}
