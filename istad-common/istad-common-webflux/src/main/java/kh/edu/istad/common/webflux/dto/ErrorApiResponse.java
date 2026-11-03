package kh.edu.istad.common.webflux.dto;

import java.util.Map;

public record ErrorApiResponse(
        String message,
        int status,
        Map<String, Object> details
) {
}
