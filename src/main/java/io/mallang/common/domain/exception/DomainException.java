package io.mallang.common.domain.exception;

public abstract class DomainException extends RuntimeException {

    public DomainException(String message) {
        super(message);
    }
}
