package kh.edu.istad.platform.iam.domain.valueobject;

import java.util.UUID;

public record UserId(UUID value) {
    public UUID getValue() {
        return value;
    }
}
