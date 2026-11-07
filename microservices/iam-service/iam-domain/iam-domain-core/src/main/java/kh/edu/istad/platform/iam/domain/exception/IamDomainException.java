package kh.edu.istad.platform.iam.domain.exception;

import kh.edu.istad.common.domain.exception.DomainException;

public class IamDomainException extends DomainException {

    public IamDomainException(String message) {
        super(message);
    }

    public IamDomainException(String message, Throwable cause) {
        super(message, cause);
    }
}

