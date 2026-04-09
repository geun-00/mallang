package io.mallang.common.domain.exception;

public class AggregateNotLoadedException extends DomainException {

    public AggregateNotLoadedException(String message) {
        super(message);
    }
}
