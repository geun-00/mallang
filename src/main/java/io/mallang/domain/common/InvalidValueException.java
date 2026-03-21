package io.mallang.domain.common;

public class InvalidValueException extends RuntimeException {

    public InvalidValueException(String message) {
        super(message);
    }
}
