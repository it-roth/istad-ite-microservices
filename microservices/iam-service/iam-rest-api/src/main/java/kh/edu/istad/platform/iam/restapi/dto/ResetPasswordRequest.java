package kh.edu.istad.platform.iam.restapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ResetPasswordRequest(
        @NotBlank(message = "New password is required")
        String newPassword,

        @NotBlank(message = "Confirm password is required")
        String confirmPassword,

        String code
) {
    public ResetPasswordRequest(String newPassword, String confirmPassword) {
        this(newPassword, confirmPassword, null);
    }
}


