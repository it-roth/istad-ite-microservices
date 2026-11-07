package kh.edu.istad.platform.iam.restapi.dto;

public record AddressDto(
        String streetAddress,
        String locality,
        String region,
        String postalCode,
        String country
) {
}


