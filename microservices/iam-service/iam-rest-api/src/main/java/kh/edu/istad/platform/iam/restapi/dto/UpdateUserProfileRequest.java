package kh.edu.istad.platform.iam.restapi.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record UpdateUserProfileRequest(
        @Size(min = 3, max = 50)
        String username,
        @Email
        @Size(max = 255)
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
        @Valid AddressDto address
) {
    public UpdateUserProfileRequest(
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
            AddressDto address
    ) {
        this(null, email, givenName, familyName, middleName, nickname, gender, birthdate, profileUrl, pictureUrl, websiteUrl, zoneinfo, locale, phoneNumber, address);
    }
}

