package io.mallang.member.domain.exception;

import io.mallang.common.domain.exception.DomainException;

public class InvalidMemberStateException extends DomainException {

    public InvalidMemberStateException(String message) {
        super(message);
    }
}
