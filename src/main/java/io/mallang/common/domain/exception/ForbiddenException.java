package io.mallang.common.domain.exception;

public abstract class ForbiddenException extends DomainException {

    private final String clientMessage;

    public ForbiddenException(String message) {
        super(message);
        this.clientMessage = message;
    }

    public ForbiddenException(String clientMessage, String logMessage) {
        super(logMessage);
        this.clientMessage = clientMessage;
    }

    public String getClientMessage() {
        return clientMessage;
    }
}
