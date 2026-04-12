package io.mallang.member.domain.exception;

import io.mallang.common.domain.exception.DomainNotFoundException;
import io.mallang.member.domain.Email;
import io.mallang.member.domain.MemberId;

public class MemberNotFoundException extends DomainNotFoundException {

    public MemberNotFoundException(MemberId memberId) {
        super("회원을 찾을 수 없습니다.", "Member를 찾을 수 없습니다 => id: " + memberId.value());
    }

    public MemberNotFoundException(Email email) {
        super("회원을 찾을 수 없습니다.", "Member를 찾을 수 없습니다 => email: " + email.address());
    }
}
