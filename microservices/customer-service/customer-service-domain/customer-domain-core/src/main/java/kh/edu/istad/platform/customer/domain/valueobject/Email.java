package kh.edu.istad.platform.customer.domain.valueobject;

public record Email(String value) {

    public String getValue() {
        return value;
    }
}