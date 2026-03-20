package io.mallang.member.domain;

public class DuplicateEmailException extends RuntimeException {

    public DuplicateEmailException(Email email) {
        super("이미 사용 중인 이메일입니다: " + email.address());
    }
}
