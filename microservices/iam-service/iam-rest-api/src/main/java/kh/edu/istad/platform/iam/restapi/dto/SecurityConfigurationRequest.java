package kh.edu.istad.platform.iam.restapi.dto;

import jakarta.validation.constraints.Min;

public record SecurityConfigurationRequest(
        boolean mfaRequired,
        @Min(1) int maxFailedLoginAttempts,
        @Min(1) int sessionTimeoutMinutes,
        @Min(6) int passwordMinimumLength,
        boolean passwordRequireUppercase,
        boolean passwordRequireNumber,
        boolean passwordRequireSymbol
) {}
