package kh.edu.istad.platform.iam.domain.dto;

import java.util.List;

public record PageResult<T>(
        List<T> content,
        int pageNumber,
        int pageSize,
        long totalElements,
        int totalPages,
        boolean isFirst,
        boolean isLast
) {}

