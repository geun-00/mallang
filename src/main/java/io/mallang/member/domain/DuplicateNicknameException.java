package io.mallang.member.domain;

import io.mallang.domain.common.DuplicateException;

public class DuplicateNicknameException extends DuplicateException {

    public DuplicateNicknameException(Nickname nickname) {
        super("이미 사용 중인 닉네임입니다: " + nickname.value());
    }
}
