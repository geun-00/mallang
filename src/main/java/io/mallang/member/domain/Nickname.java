package io.mallang.member.domain;

import java.util.regex.Pattern;

public record Nickname(String value) {

    private static final Pattern NICKNAME_PATTERN =
            Pattern.compile("^[가-힣a-zA-Z0-9_.\\-]([가-힣a-zA-Z0-9_.\\- ]*[가-힣a-zA-Z0-9_.\\-])?$");

    public Nickname {
        if (value == null || value.isEmpty()) throw new IllegalArgumentException();
        if (value.startsWith(" ") ||  value.endsWith(" ")) throw new IllegalArgumentException();
        if (value.length() < 2 || value.length() > 20) throw new IllegalArgumentException();
        if (!NICKNAME_PATTERN.matcher(value).matches()) throw new IllegalArgumentException();
    }
}
