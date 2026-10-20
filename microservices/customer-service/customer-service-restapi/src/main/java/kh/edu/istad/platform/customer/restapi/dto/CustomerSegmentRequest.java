package kh.edu.istad.platform.customer.restapi.dto;

import jakarta.validation.constraints.NotBlank;

public record CustomerSegmentRequest(
        @NotBlank
        String type
) {
}
