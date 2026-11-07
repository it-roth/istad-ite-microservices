package kh.edu.istad.platform.iam.restapi.exception;

import kh.edu.istad.common.domain.exception.DomainException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestControllerAdvice(basePackages = "kh.edu.istad.platform.iam.restapi.controller")
public class GlobalRestExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleValidationException(
            MethodArgumentNotValidException ex
    ) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setType(URI.create("https://iam.istad.co/problems/validation-error"));
        problemDetail.setTitle("Validation failed");

        List<FieldErrorItem> errors = new ArrayList<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            errors.add(new FieldErrorItem(fieldError.getField(), fieldError.getDefaultMessage()));
        }

        problemDetail.setProperty("errors", errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problemDetail);
    }

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ProblemDetail> handleDomainException(
            DomainException ex
    ) {
        log.warn("Domain exception: {}", ex.getMessage());
        String msg = ex.getMessage() != null ? ex.getMessage() : "Domain rule violation";
        String lower = msg.toLowerCase();

        HttpStatus status;
        String type;
        String title;

        if (lower.contains("not found")) {
            status = HttpStatus.NOT_FOUND;
            type = "https://iam.istad.co/problems/resource-not-found";
            title = "Resource not found";
        } else if (lower.contains("already exists") || lower.contains("conflict") || lower.contains("already registered")) {
            status = HttpStatus.CONFLICT;
            type = "https://iam.istad.co/problems/conflict";
            title = "Resource conflict";
        } else if (lower.contains("system") || lower.contains("protected") || lower.contains("cannot be deleted") || lower.contains("cannot be changed")) {
            status = HttpStatus.FORBIDDEN;
            type = "https://iam.istad.co/problems/forbidden-operation";
            title = "Operation not allowed";
        } else {
            status = HttpStatus.UNPROCESSABLE_ENTITY;
            type = "https://iam.istad.co/problems/business-rule-violation";
            title = "Business rule violation";
        }

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, msg);
        problemDetail.setType(URI.create(type));
        problemDetail.setTitle(title);
        return ResponseEntity.status(status).body(problemDetail);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ProblemDetail> handleAccessDeniedException(
            AccessDeniedException ex
    ) {
        log.warn("Access denied: {}", ex.getMessage());
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.FORBIDDEN,
                "You do not have the required authorities to access this resource"
        );
        problemDetail.setType(URI.create("https://iam.istad.co/problems/access-denied"));
        problemDetail.setTitle("Access denied");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(problemDetail);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ProblemDetail> handleAuthenticationException(
            AuthenticationException ex
    ) {
        log.warn("Authentication failed: {}", ex.getMessage());
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.UNAUTHORIZED,
                "Authentication is required to access this resource"
        );
        problemDetail.setType(URI.create("https://iam.istad.co/problems/unauthorized"));
        problemDetail.setTitle("Unauthorized");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(problemDetail);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ProblemDetail> handleIllegalArgumentException(
            IllegalArgumentException ex
    ) {
        log.warn("Illegal argument: {}", ex.getMessage());
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                ex.getMessage()
        );
        problemDetail.setType(URI.create("https://iam.istad.co/problems/bad-request"));
        problemDetail.setTitle("Bad request");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problemDetail);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ProblemDetail> handleIllegalStateException(
            IllegalStateException ex
    ) {
        log.warn("Illegal state: {}", ex.getMessage());
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                ex.getMessage()
        );
        problemDetail.setType(URI.create("https://iam.istad.co/problems/illegal-state"));
        problemDetail.setTitle("Illegal state");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problemDetail);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleGenericException(
            Exception ex
    ) {
        log.error("Internal server error: ", ex);
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected internal error occurred"
        );
        problemDetail.setType(URI.create("https://iam.istad.co/problems/internal-error"));
        problemDetail.setTitle("Internal server error");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problemDetail);
    }
}
