package kh.edu.istad.platform.customer.restapi.exception;

import kh.edu.istad.platform.customer.domain.exception.CustomerConflictException;
import kh.edu.istad.platform.customer.domain.exception.CustomerDomainException;
import kh.edu.istad.platform.customer.domain.exception.CustomerNotFoundException;
import kh.edu.istad.platform.customer.domain.exception.CustomerSegmentNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.yaml.snakeyaml.constructor.DuplicateKeyException;

import java.util.Map;

@RestControllerAdvice
public class CustomerExceptionHandler {
    @ExceptionHandler({
            CustomerNotFoundException.class,
            CustomerSegmentNotFoundException.class
    })
    public ResponseEntity<Map<String, String>> handleNotFound(RuntimeException exception) {
        return error(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler({
            CustomerConflictException.class,
            DuplicateKeyException.class
    })
    public ResponseEntity<Map<String, String>> handleConflict(RuntimeException exception) {
        return error(HttpStatus.CONFLICT, exception.getMessage());
    }

    @ExceptionHandler({
            CustomerDomainException.class,
            IllegalArgumentException.class
    })
    public ResponseEntity<Map<String, String>> handleInvalidInput(RuntimeException exception) {
        return error(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    private ResponseEntity<Map<String, String>> error(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(Map.of("message", message));
    }
}
