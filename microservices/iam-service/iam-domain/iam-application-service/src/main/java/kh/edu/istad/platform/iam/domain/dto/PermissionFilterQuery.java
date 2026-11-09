package kh.edu.istad.platform.iam.domain.dto;

public record PermissionFilterQuery(
        String resource,
        String action,
        int page,
        int size,
        String sort
) {
    public PermissionFilterQuery(String resource, String action, int page, int size) {
        this(resource, action, page, size, null);
    }
}
