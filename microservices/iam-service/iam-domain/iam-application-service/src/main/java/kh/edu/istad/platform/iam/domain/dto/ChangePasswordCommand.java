package kh.edu.istad.platform.iam.domain.dto;

import java.util.UUID;

public record ChangePasswordCommand(
        UUID userUuid,
        String oldPassword,
        String newPassword
) {}

