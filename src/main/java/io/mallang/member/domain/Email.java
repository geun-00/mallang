package io.mallang.member.domain;

import java.util.regex.Pattern;

public record Email(String address) {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_]([A-Za-z0-9+_.-]*[A-Za-z0-9+_])?@[A-Za-z0-9]([A-Za-z0-9.-]*[A-Za-z0-9])?\\.[A-Za-z]{2,}$");

    public Email {
        if (address == null || address.isBlank()) {
            throw new IllegalArgumentException("이메일은 비어 있을 수 없습니다.");
        }

        if (!EMAIL_PATTERN.matcher(address).matches()) {
            throw new IllegalArgumentException("유효하지 않은 이메일 형식입니다: " + address);
        }
    }
}