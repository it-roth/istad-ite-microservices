package kh.edu.istad.platform.customer.domain.exception;

public class CustomerConflictException extends RuntimeException {
    public CustomerConflictException(String message) {
        super(message);
    }
}
