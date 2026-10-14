package kh.edu.istad.platform.order.domain.valueobject;

import java.util.UUID;

public record OrderItemId(UUID Value) {
    public UUID value() {
        return Value;
    }
}
