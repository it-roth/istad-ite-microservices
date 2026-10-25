package kh.edu.istad.platform.order.domain.valueobject;

import kh.edu.istad.platform.order.domain.exception.OrderDomainException;

import java.util.UUID;

public record StreetAddress(
        UUID id,
        String street,
        String postalCode,
        String city
) {
    public StreetAddress(String street, String postalCode, String city) {
        this(UUID.randomUUID(), street, postalCode, city);
    }

    public void validate() {
        if (id == null) {
            throw new OrderDomainException("Street address ID is required");
        }
        if (street == null || street.isBlank()) {
            throw new OrderDomainException("Street address is required");
        }
        if (postalCode == null || postalCode.isBlank()) {
            throw new OrderDomainException("Postal code is required");
        }
        if (city == null || city.isBlank()) {
            throw new OrderDomainException("City is required");
        }
    }
}
