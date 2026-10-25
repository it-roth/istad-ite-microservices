package kh.edu.istad.platform.order.dto;

public record CustomerResponseDto(
        String id,
        String username,
        String familyName,
        String givenName,
        Object email,
        Object phoneNumber,
        String status
) {
}
