package io.mallang.common.domain.exception;

public class DuplicateException extends DomainException {

    public DuplicateException(String message) {
        super(message);
    }

    public DuplicateException(String clientMessage, String logMessage) {
        super(clientMessage, logMessage);
    }
}
