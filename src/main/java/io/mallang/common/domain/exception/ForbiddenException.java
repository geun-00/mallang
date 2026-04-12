package io.mallang.common.domain.exception;

public abstract class ForbiddenException extends DomainException {

    protected ForbiddenException(String message) {
        super(message);
    }

    protected ForbiddenException(String clientMessage, String logMessage) {
        super(clientMessage, logMessage);
    }
}
