package kh.edu.istad.platform.iam.domain.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record UserResult(
        UUID uuid,
        String username,
        String email,
        Boolean emailVerified,
        String givenName,
        String familyName,
        String middleName,
        String nickname,
        String gender,
        LocalDate birthdate,
        String profileUrl,
        String pictureUrl,
        String websiteUrl,
        String zoneinfo,
        String locale,
        String phoneNumber,
        Boolean phoneNumberVerified,
        Boolean enabled,
        Boolean accountNonLocked,
        LocalDateTime lastLoginAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        String streetAddress,
        String locality,
        String region,
        String postalCode,
        String country,
        Boolean twoFactorEnabled
) {
    public UserResult(
            UUID uuid,
            String username,
            String email,
            Boolean emailVerified,
            String givenName,
            String familyName,
            String middleName,
            String nickname,
            String gender,
            LocalDate birthdate,
            String profileUrl,
            String pictureUrl,
            String websiteUrl,
            String zoneinfo,
            String locale,
            String phoneNumber,
            Boolean phoneNumberVerified,
            Boolean enabled,
            Boolean accountNonLocked,
            LocalDateTime lastLoginAt,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        this(uuid, username, email, emailVerified, givenName, familyName, middleName, nickname, gender, birthdate, profileUrl, pictureUrl, websiteUrl, zoneinfo, locale, phoneNumber, phoneNumberVerified, enabled, accountNonLocked, lastLoginAt, createdAt, updatedAt, null, null, null, null, null, false);
    }
}
