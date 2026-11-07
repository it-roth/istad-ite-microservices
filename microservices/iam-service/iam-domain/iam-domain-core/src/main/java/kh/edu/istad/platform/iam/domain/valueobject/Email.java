package kh.edu.istad.platform.iam.domain.valueobject;

public record Email(String value) {
    public Email {
        if (value != null) {
            value = value.toLowerCase().trim();
        }
    }
}