package io.mallang.common.domain.exception;

public class InvalidValueException extends DomainException {

    public InvalidValueException(String message) {
        super("요청 값이 올바르지 않습니다.", message);
    }
}
