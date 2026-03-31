package io.mallang.domain.common.exception;

public abstract class DomainNotFoundException extends DomainException {

    public DomainNotFoundException(String message) {
        super(message);
    }
}
