package kh.edu.istad.platform.business.domain.entity;

import kh.edu.istad.common.domain.entity.BaseEntity;
import kh.edu.istad.common.domain.valueobject.BusinessId;
import kh.edu.istad.common.domain.valueobject.OrderId;

import java.util.UUID;

public class OrderApproval extends BaseEntity<UUID> {

    private final OrderId orderId;
    private final BusinessId businessId;

    // Getters
    public OrderId getOrderId() {
        return orderId;
    }

    public BusinessId getBusinessId() {
        return businessId;
    }

    // Builder
    private OrderApproval(Builder builder) {
        super.setId(UUID.randomUUID());
        this.orderId = builder.orderId;
        this.businessId = builder.businessId;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private OrderId orderId;
        private BusinessId businessId;

        public Builder orderId(OrderId orderId) {
            this.orderId = orderId;
            return this;
        }

        public Builder businessId(BusinessId businessId) {
            this.businessId = businessId;
            return this;
        }

        public OrderApproval build() {
            return new OrderApproval(this);
        }
    }
}
