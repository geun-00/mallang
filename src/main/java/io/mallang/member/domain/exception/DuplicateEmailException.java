package io.mallang.member.domain.exception;

import io.mallang.domain.common.exception.DuplicateException;
import io.mallang.member.domain.Email;

public class DuplicateEmailException extends DuplicateException {

    public DuplicateEmailException(Email email) {
        super("이미 사용 중인 이메일입니다: " + email.address());
    }
}
