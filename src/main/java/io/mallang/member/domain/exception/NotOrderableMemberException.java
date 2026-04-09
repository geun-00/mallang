package io.mallang.member.domain.exception;

import io.mallang.common.domain.exception.ForbiddenException;
import io.mallang.member.domain.MemberId;

public class NotOrderableMemberException extends ForbiddenException {

    public NotOrderableMemberException(MemberId memberId) {
        super("주문할 수 없는 회원입니다. memberId=" + memberId.value());
    }
}
