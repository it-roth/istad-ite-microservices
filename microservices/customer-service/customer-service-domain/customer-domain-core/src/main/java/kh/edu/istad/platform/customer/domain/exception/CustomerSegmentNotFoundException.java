package kh.edu.istad.platform.customer.domain.exception;

public class CustomerSegmentNotFoundException extends RuntimeException {
    public CustomerSegmentNotFoundException(String id) {
        super("Customer segment not found: " + id);
    }
}
