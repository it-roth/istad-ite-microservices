package kh.edu.istad.platform.iam.domain.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record CreateUserCommand(
        String username,
        String email,
        String rawPassword,
        String givenName,
        String familyName,
        String middleName,
        String nickname,
        String gender,
        LocalDate birthdate,
        String locale,
        String phoneNumber,
        String streetAddress,
        String locality,
        String region,
        String postalCode,
        String country,
        List<UUID> roleUuids
) {}

