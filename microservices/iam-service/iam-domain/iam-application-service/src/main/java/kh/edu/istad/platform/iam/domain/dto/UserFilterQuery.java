package kh.edu.istad.platform.iam.domain.dto;

public record UserFilterQuery(
        String query,
        Boolean enabled,
        int page,
        int size
) {}

