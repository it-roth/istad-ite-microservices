package kh.edu.istad.platform.iam.domain.dto;

public record SecurityConfigurationResult(
        boolean mfaRequired,
        int maxFailedLoginAttempts,
        int sessionTimeoutMinutes,
        int passwordMinimumLength,
        boolean passwordRequireUppercase,
        boolean passwordRequireNumber,
        boolean passwordRequireSymbol
) {}
