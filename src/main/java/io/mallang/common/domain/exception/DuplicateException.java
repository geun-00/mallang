package io.mallang.common.domain.exception;

public class DuplicateException extends DomainException {

    private final String clientMessage;

    public DuplicateException(String message) {
        super(message);
        this.clientMessage = message;
    }

    public DuplicateException(String clientMessage, String logMessage) {
        super(logMessage);
        this.clientMessage = clientMessage;
    }

    public String getClientMessage() {
        return clientMessage;
    }
}
