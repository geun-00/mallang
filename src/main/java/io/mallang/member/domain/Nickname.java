package io.mallang.member.domain;

import java.util.regex.Pattern;

public record Nickname(String value) {

    private static final Pattern NICKNAME_PATTERN =
            Pattern.compile("^[가-힣a-zA-Z0-9_.\\-]([가-힣a-zA-Z0-9_.\\- ]*[가-힣a-zA-Z0-9_.\\-])?$");

    public Nickname {
        if (value == null || value.isBlank())
            throw new IllegalArgumentException("닉네임은 비어 있을 수 없습니다.");
        if (!value.equals(value.strip()))
            throw new IllegalArgumentException("닉네임은 공백으로 시작하거나 끝날 수 없습니다.");
        if (value.length() < 2 || value.length() > 20)
            throw new IllegalArgumentException("닉네임은 2자 이상 20자 이하여야 합니다.");
        if (!NICKNAME_PATTERN.matcher(value).matches())
            throw new IllegalArgumentException("닉네임은 한글, 영문자, 숫자, 밑줄(_), 점(.), 하이픈(-)만 사용할 수 있습니다.");
    }
}
