package kh.edu.istad.platform.order.domain.valueobject;

import kh.edu.istad.platform.order.domain.exception.OrderDomainException;

public record StreetAddress(
        String street,
        String postalCode,
        String city
) {
    public void validate() {
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
