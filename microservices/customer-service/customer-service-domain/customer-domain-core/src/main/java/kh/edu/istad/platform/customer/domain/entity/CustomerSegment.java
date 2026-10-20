
package kh.edu.istad.platform.customer.domain.entity;

import kh.edu.istad.common.domain.entity.AuditableEntity;
import kh.edu.istad.common.domain.valueobject.CustomerSegmentId;
import kh.edu.istad.platform.customer.domain.exception.CustomerDomainException;

import java.util.UUID;

public class CustomerSegment extends AuditableEntity<CustomerSegmentId> {

    private String type;

    public String getType() {
        return type;
    }

    public void initiateCustomerSegment() {
        validateInitiate();
        super.setId(new CustomerSegmentId(UUID.randomUUID()));
    }

    private void validateInitiate() {
        if (getId() != null) {
            throw new CustomerDomainException(
                    "Customer segment is already initialized"
            );
        }

        if (type == null || type.isBlank()) {
            throw new CustomerDomainException(
                    "Customer segment type must not be blank"
            );
        }
    }

    private CustomerSegment(Builder builder) {
        super.setId(builder.id);
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

        public Builder id(CustomerSegmentId val) {
            id = val;
            return this;
        }

        public Builder type(String val) {
            type = val;
            return this;
        }

        public CustomerSegment build() {
            return new CustomerSegment(this);
        }
    }
}
