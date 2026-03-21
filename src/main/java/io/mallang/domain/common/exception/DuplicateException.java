package io.mallang.domain.common.exception;

public class DuplicateException extends RuntimeException {

    public DuplicateException(String message) {
        super(message);
    }
}
