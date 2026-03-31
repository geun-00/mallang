package io.mallang.member.domain.exception;

import io.mallang.domain.common.exception.DomainException;

public class InvalidMemberStateException extends DomainException {

    public InvalidMemberStateException(String message) {
        super(message);
    }
}
