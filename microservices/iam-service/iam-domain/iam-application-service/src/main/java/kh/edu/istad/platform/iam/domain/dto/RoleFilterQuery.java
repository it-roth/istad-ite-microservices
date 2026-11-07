package kh.edu.istad.platform.iam.domain.dto;

public record RoleFilterQuery(
        String name,
        Boolean isActive,
        int page,
        int size) {
}
