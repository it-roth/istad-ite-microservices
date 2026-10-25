package kh.edu.istad.platform.business.restapi.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record CreateBusinessRequest(
        @NotBlank(message = "Business name is required")
        String businessName,

        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        String email,

        @NotBlank(message = "Phone number is required")
        String phoneNumber,

        @NotEmpty(message = "Products list must not be empty")
        @Valid
        List<ProductRequest> products
) {
}
