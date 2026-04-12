package io.mallang.common.domain.exception;

import lombok.Getter;

@Getter
public abstract class DomainException extends RuntimeException {

    private final String clientMessage;

    protected DomainException(String message) {
        this("요청을 처리할 수 없습니다.", message);
    }

    protected DomainException(String clientMessage, String logMessage) {
        super(logMessage);
        this.clientMessage = clientMessage;
    }
}
