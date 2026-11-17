package kh.edu.istad.platform.iam.restapi.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record UserResponse(
        UUID uuid,
        String username,
        String email,
        Boolean emailVerified,
        String name,
        String givenName,
        String familyName,
        String middleName,
        String nickname,
        String gender,
        LocalDate birthdate,
        String phoneNumber,
        String pictureUrl,
        String websiteUrl,
        String locale,
        Boolean enabled,
        List<UserRoleSummaryResponse> roles,
        LocalDateTime lastLoginAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        AddressDto address,
        Boolean twoFactorEnabled) {
    public UserResponse(
            UUID uuid,
            String username,
            String email,
            Boolean emailVerified,
            String name,
            String givenName,
            String familyName,
            String middleName,
            String nickname,
            String gender,
            LocalDate birthdate,
            String phoneNumber,
            String pictureUrl,
            String websiteUrl,
            String locale,
            Boolean enabled,
            List<UserRoleSummaryResponse> roles,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            AddressDto address) {
        this(uuid, username, email, emailVerified, name, givenName, familyName, middleName, nickname, gender, birthdate,
                phoneNumber, pictureUrl, websiteUrl, locale, enabled, roles, null, createdAt, updatedAt, address, false);
    }
}
