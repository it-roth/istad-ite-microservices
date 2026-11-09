package kh.edu.istad.platform.iam.domain.dto;

public record UserFilterQuery(
        String query,
        Boolean enabled,
        int page,
        int size,
        String sort
) {
    public UserFilterQuery(String query, Boolean enabled, int page, int size) {
        this(query, enabled, page, size, null);
    }
}
