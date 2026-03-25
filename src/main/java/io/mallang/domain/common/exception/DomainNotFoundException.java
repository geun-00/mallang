package io.mallang.domain.common.exception;

public class DomainNotFoundException extends RuntimeException {

    public DomainNotFoundException(String message) {
        super(message);
    }
}
