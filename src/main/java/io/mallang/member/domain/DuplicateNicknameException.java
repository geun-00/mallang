package io.mallang.member.domain;

public class DuplicateNicknameException extends RuntimeException {

    public DuplicateNicknameException(Nickname nickname) {
        super("이미 사용 중인 닉네임입니다: " + nickname.value());
    }
}
