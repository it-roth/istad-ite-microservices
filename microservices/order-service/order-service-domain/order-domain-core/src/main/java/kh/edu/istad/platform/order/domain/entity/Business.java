package kh.edu.istad.platform.order.domain.entity;

import kh.edu.istad.common.domain.entity.AggregateRoot;
import kh.edu.istad.common.domain.valueobject.BusinessId;

import java.util.List;

public class Business extends AggregateRoot<BusinessId> {

    private final List<Product> products;
    private boolean active;

    public List<Product> getProducts() {
        return products;
    }

    public boolean isActive() {
        return active;
    }

    private Business(Builder builder) {
        super.setId(builder.businessId);
        this.products = builder.products;
        this.active = builder.active;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private BusinessId businessId;
        private List<Product> products;
        private boolean active;

        public Builder businessId(BusinessId businessId) {
            this.businessId = businessId;
            return this;
        }

        public Builder products(List<Product> products) {
            this.products = products;
            return this;
        }

        public Builder active(boolean active) {
            this.active = active;
            return this;
        }

        public Business build() {
            return new Business(this);
        }
    }
}
