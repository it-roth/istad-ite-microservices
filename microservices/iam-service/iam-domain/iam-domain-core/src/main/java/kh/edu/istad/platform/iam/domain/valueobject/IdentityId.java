package kh.edu.istad.platform.iam.domain.valueobject;

import java.util.UUID;

public record IdentityId(UUID value) {
    public UUID getValue() {
        return value;
    }
}
