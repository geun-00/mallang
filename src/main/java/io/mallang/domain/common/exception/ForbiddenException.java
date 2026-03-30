package io.mallang.domain.common.exception;

public abstract class ForbiddenException extends DomainException {

    public ForbiddenException(String message) {
        super(message);
    }
}
