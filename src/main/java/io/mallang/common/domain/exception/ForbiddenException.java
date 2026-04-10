package io.mallang.common.domain.exception;

public abstract class ForbiddenException extends DomainException {

    public ForbiddenException(String message) {
        super(message);
    }
}
