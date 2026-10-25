package kh.edu.istad.platform.order.restapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record OrderAddressRequest(
        @NotBlank(message = "Street is required")
        @Size(max = 100, message = "Street must not exceed 100 characters")
        String street,

        @NotBlank(message = "Postal code is required")
        @Size(max = 10, message = "Postal code must not exceed 10 characters")
        String postalCode,

        @NotBlank(message = "City is required")
        @Size(max = 50, message = "City must not exceed 50 characters")
        String city
) {}
