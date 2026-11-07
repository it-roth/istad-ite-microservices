package kh.edu.istad.platform.iam.restapi.dto;

import jakarta.validation.Valid;

import java.time.LocalDate;

public record UpdateUserProfileRequest(
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
}


