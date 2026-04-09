package io.mallang.member.domain;

import io.mallang.common.domain.exception.InvalidValueException;

public record MemberId(String value) {

    public MemberId {
        if (value == null || value.isBlank()) {
            throw new InvalidValueException("MemberId는 비어있을 수 없습니다.");
        }
    }
}
