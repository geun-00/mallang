package io.mallang.domain.common.exception;

public class AggregateNotLoadedException extends DomainException {

    public AggregateNotLoadedException(String message) {
        super(message);
    }
}
