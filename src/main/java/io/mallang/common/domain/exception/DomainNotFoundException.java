package io.mallang.common.domain.exception;

public abstract class DomainNotFoundException extends DomainException {

    public DomainNotFoundException(String message) {
        super(message);
    }
}
