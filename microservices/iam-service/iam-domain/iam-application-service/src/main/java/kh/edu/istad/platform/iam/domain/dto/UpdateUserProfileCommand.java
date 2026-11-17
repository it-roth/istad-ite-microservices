package kh.edu.istad.platform.iam.domain.dto;

import java.time.LocalDate;
import java.util.UUID;

public record UpdateUserProfileCommand(
        UUID userUuid,
        String username,
        String email,
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
        String streetAddress,
        String locality,
        String region,
        String postalCode,
        String country
) {
    public UpdateUserProfileCommand(
            UUID userUuid,
            String email,
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
            String streetAddress,
            String locality,
            String region,
            String postalCode,
            String country
    ) {
        this(userUuid, null, email, givenName, familyName, middleName, nickname, gender, birthdate, profileUrl, pictureUrl, websiteUrl, zoneinfo, locale, phoneNumber, streetAddress, locality, region, postalCode, country);
    }
}
