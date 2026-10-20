package kh.edu.istad.platform.customer.domain.entity;

import kh.edu.istad.common.domain.entity.AggregateRoot;
import kh.edu.istad.common.domain.valueobject.CustomerSegmentId;
import kh.edu.istad.platform.customer.domain.exception.CustomerDomainException;

import java.util.UUID;

public class CustomerSegment extends AggregateRoot<CustomerSegmentId> {
    private String type;

    public String getType() {
        return type;
    }

    public void initiateCustomerSegment() {
        if (getId() != null) {
            throw new CustomerDomainException("Customer segment is already initialized");
        }
        validateType(type);
        type = type.trim();
        setId(new CustomerSegmentId(UUID.randomUUID()));
    }

    public void validateForUpdate() {
        validateType(type);
    }

    public void updateType(String type) {
        validateType(type);
        this.type = type.trim();
    }

    private void validateType(String type) {
        if (type == null || type.isBlank()) {
            throw new CustomerDomainException("Customer segment type must not be blank");
        }
    }

    private CustomerSegment(Builder builder) {
        setId(builder.id);
        type = builder.type;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private CustomerSegmentId id;
        private String type;

        private Builder() {
        }

        public Builder id(CustomerSegmentId id) {
            this.id = id;
            return this;
        }

        public Builder type(String type) {
            this.type = type;
            return this;
        }

        public CustomerSegment build() {
            return new CustomerSegment(this);
        }
    }
}
