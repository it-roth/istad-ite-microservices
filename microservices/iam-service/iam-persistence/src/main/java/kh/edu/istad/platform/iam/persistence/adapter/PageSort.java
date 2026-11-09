package kh.edu.istad.platform.iam.persistence.adapter;

import org.springframework.data.domain.Sort;

import java.util.Set;

final class PageSort {

    private PageSort() {
    }

    static Sort parse(String value, Sort defaultSort, Set<String> allowedProperties) {
        if (value == null || value.isBlank()) {
            return defaultSort;
        }

        String[] parts = value.split(",", -1);
        String property = parts[0].trim();
        if (!allowedProperties.contains(property)) {
            throw new IllegalArgumentException("Unsupported sort property: " + property);
        }
        if (parts.length > 2 || (parts.length == 2 && parts[1].isBlank())) {
            throw new IllegalArgumentException("Sort must use the format property[,asc|desc]");
        }

        Sort.Direction direction = parts.length == 1
                ? Sort.Direction.ASC
                : Sort.Direction.fromString(parts[1].trim());
        return Sort.by(direction, property);
    }
}
