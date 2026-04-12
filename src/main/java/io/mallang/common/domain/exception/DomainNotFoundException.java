package io.mallang.common.domain.exception;

public abstract class DomainNotFoundException extends DomainException {

    private final String clientMessage;

    protected DomainNotFoundException(String message) {
        super(message);
        this.clientMessage = message;
    }

    protected DomainNotFoundException(String clientMessage, String logMessage) {
        super(logMessage);
        this.clientMessage = clientMessage;
    }

    public String getClientMessage() {
        return clientMessage;
    }
}
