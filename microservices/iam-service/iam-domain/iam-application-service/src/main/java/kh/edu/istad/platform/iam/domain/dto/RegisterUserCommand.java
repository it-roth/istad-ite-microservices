package kh.edu.istad.platform.iam.domain.dto;

public record RegisterUserCommand(
        String username,
        String email,
        String rawPassword,
        String givenName,
        String familyName,
        String locale
) {}

