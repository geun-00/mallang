package io.mallang.member.domain.exception;

import io.mallang.common.domain.exception.DomainException;
import io.mallang.member.domain.MemberId;
import io.mallang.member.domain.MemberStatus;

public class InvalidMemberStateException extends DomainException {

    public InvalidMemberStateException(String message) {
        super(message, message);
    }

    public InvalidMemberStateException(String clientMessage, MemberId memberId, MemberStatus currentStatus) {
        super(
                clientMessage,
                "%s => memberId: %s, currentStatus: %s".formatted(clientMessage, memberId.value(), currentStatus)
        );
    }
}
