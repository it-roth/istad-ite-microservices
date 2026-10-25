package kh.edu.istad.common.domain.valueobject;

import java.util.UUID;

public record CustomerSegmentId(UUID value) {
    public CustomerSegmentId {
        if (value == null) {
            throw new IllegalArgumentException("Customer segment id must not be null");
        }
    }
}
