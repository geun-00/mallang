package io.mallang.common.domain.exception;

public abstract class DomainNotFoundException extends DomainException {

    protected DomainNotFoundException(String message) {
        super(message);
    }

    protected DomainNotFoundException(String clientMessage, String logMessage) {
        super(clientMessage, logMessage);
    }
}
