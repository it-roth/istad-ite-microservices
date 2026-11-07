package kh.edu.istad.platform.iam.restapi.dto;

public record PageMetadata(
        int number,
        int size,
        long totalElements,
        int totalPages
) {
}


