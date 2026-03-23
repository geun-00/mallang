package io.mallang.member.domain.exception;

import io.mallang.member.domain.Email;
import io.mallang.member.domain.MemberId;

public class MemberNotFoundException extends RuntimeException {

    public MemberNotFoundException(MemberId memberId) {
        super("Member를 찾을 수 없습니다. id: " + memberId.value());
    }

    public MemberNotFoundException(Email email) {
        super("Member를 찾을 수 없습니다. email: " + email.address());
    }
}
