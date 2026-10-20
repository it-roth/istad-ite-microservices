package kh.edu.istad.platform.customer.domain.entity;

import kh.edu.istad.common.domain.entity.AggregateRoot;
import kh.edu.istad.common.domain.valueobject.CustomerId;
import kh.edu.istad.common.domain.valueobject.CustomerSegmentId;
import kh.edu.istad.platform.customer.domain.exception.CustomerDomainException;
import kh.edu.istad.platform.customer.domain.valueobject.CustomerStatus;
import kh.edu.istad.platform.customer.domain.valueobject.Email;
import kh.edu.istad.platform.customer.domain.valueobject.PhoneNumber;

import java.util.UUID;

public class Customer extends AggregateRoot<CustomerId> {
    private String username;
    private String familyName;
    private String givenName;
    private Email email;
    private PhoneNumber phoneNumber;
    private CustomerSegmentId segmentId;
    private CustomerStatus status;

    public void initiateCustomer() {
        validateCustomer();
        super.setId(new CustomerId(UUID.randomUUID()));
        status = CustomerStatus.ACTIVE;
    }

    private void validateCustomer() {
        if (super.getId() != null) {
            throw new CustomerDomainException("Customer id must be null");
        }
        if (status != null) {
            throw new CustomerDomainException("Customer status must be null");
        }
        validateDetails();
    }

    public void updateCustomer(
            String username,
            String familyName,
            String givenName,
            Email email,
            PhoneNumber phoneNumber,
            CustomerSegmentId segmentId
    ) {
        validateDetails(username, familyName, givenName, email, phoneNumber);
        this.username = username;
        this.familyName = familyName;
        this.givenName = givenName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.segmentId = segmentId;
    }

    public void updateCustomer(String familyName, String givenName) {
        updateCustomer(username, familyName, givenName, email, phoneNumber, segmentId);
    }

    public void validateForUpdate() {
        validateDetails(username, familyName, givenName, email, phoneNumber);
    }

    public void deactivateCustomer() {
        if (status != CustomerStatus.ACTIVE) {
            throw new CustomerDomainException("Could not deactivate because customer status is not active");
        }
        status = CustomerStatus.INACTIVE;
    }

    private void validateDetails() {
        validateDetails(username, familyName, givenName, email, phoneNumber);
    }

    private static void validateDetails(
            String username,
            String familyName,
            String givenName,
            Email email,
            PhoneNumber phoneNumber
    ) {
        if (username == null || username.isBlank()) {
            throw new CustomerDomainException("Customer username must not be blank");
        }
        if (familyName == null || familyName.isBlank()) {
            throw new CustomerDomainException("Customer family name must not be blank");
        }
        if (givenName == null || givenName.isBlank()) {
            throw new CustomerDomainException("Customer given name must not be blank");
        }
        if (email == null || email.value() == null || email.value().isBlank()) {
            throw new CustomerDomainException("Customer email must not be blank");
        }
        if (phoneNumber == null || phoneNumber.value() == null || phoneNumber.value().isBlank()) {
            throw new CustomerDomainException("Customer phone number must not be blank");
        }
    }

    public String getUsername() {
        return username;
    }

    public String getFamilyName() {
        return familyName;
    }

    public String getGivenName() {
        return givenName;
    }

    public Email getEmail() {
        return email;
    }

    public PhoneNumber getPhoneNumber() {
        return phoneNumber;
    }

    public CustomerStatus getStatus() {
        return status;
    }

    public CustomerSegmentId getSegmentId() {
        return segmentId;
    }

    private Customer(Builder builder) {
        super.setId(builder.id);
        username = builder.username;
        familyName = builder.familyName;
        givenName = builder.givenName;
        email = builder.email;
        phoneNumber = builder.phoneNumber;
        segmentId = builder.segmentId;
        status = builder.status;
    }

    public static Builder builder() {
        return new Builder();
    }


    public static final class Builder {
        private CustomerId id;
        private String username;
        private String familyName;
        private String givenName;
        private Email email;
        private PhoneNumber phoneNumber;
        private CustomerSegmentId segmentId;
        private CustomerStatus status;

        private Builder() {
        }

        public Builder id(CustomerId val) {
            id = val;
            return this;
        }

        public Builder username(String val) {
            username = val;
            return this;
        }

        public Builder familyName(String val) {
            familyName = val;
            return this;
        }

        public Builder givenName(String val) {
            givenName = val;
            return this;
        }

        public Builder email(Email val) {
            email = val;
            return this;
        }

        public Builder phoneNumber(PhoneNumber val) {
            phoneNumber = val;
            return this;
        }

        public Builder segmentId(CustomerSegmentId val) {
            segmentId = val;
            return this;
        }

        public Builder status(CustomerStatus val) {
            status = val;
            return this;
        }

        public Customer build() {
            return new Customer(this);
        }
    }
}
