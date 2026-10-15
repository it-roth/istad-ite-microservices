package kh.edu.istad.platform.business.domain.entity;

import kh.edu.istad.common.domain.entity.AggregateRoot;
import kh.edu.istad.common.domain.valueobject.BusinessId;
import kh.edu.istad.platform.business.domain.exception.BusinessDomainException;
import kh.edu.istad.platform.business.domain.valueobject.BusinessContact;

import java.util.List;
import java.util.UUID;

public class Business extends AggregateRoot<BusinessId> {

    private final String businessName;
    private final BusinessContact businessContact;
    private final List<Product> products;

    private boolean active;

    public void validateBusiness() {
        if (businessName == null || businessName.isBlank()) {
            throw new BusinessDomainException("Business name is required");
        }
        if (businessContact == null) {
            throw new BusinessDomainException("Business contact is required");
        }
    }

    public void initializeBusiness() {
        super.setId(new BusinessId(UUID.randomUUID()));
        this.active = true;
    }

    // Getters
    public String getBusinessName() {
        return businessName;
    }

    public BusinessContact getBusinessContact() {
        return businessContact;
    }

    public List<Product> getProducts() {
        return products;
    }

    public boolean isActive() {
        return active;
    }

    // Builder
    private Business(Builder builder) {
        super.setId(builder.businessId);
        this.businessName = builder.businessName;
        this.businessContact = builder.businessContact;
        this.products = builder.products;
        this.active = builder.active;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private BusinessId businessId;
        private String businessName;
        private BusinessContact businessContact;
        private List<Product> products;
        private boolean active;

        public Builder businessId(BusinessId businessId) {
            this.businessId = businessId;
            return this;
        }

        public Builder businessName(String businessName) {
            this.businessName = businessName;
            return this;
        }

        public Builder businessContact(BusinessContact businessContact) {
            this.businessContact = businessContact;
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
