package kh.edu.istad.platform.iam.restapi.exception;

public record FieldErrorItem(
        String field,
        String message
) {
}
