package io.mallang.member.domain;

import java.util.regex.Pattern;

public record Receiver(String name, String phoneNumber) {

    private static final Pattern PHONE_NUMBER_PATTERN = Pattern.compile("010\\d{8}");

    public Receiver {
        if (name == null || name.isBlank())
            throw new IllegalArgumentException("이름을 입력해주세요.");

        if (name.startsWith(" ") ||  name.endsWith(" "))
            throw new IllegalArgumentException("이름은 앞뒤 공백이 없어야 합니다.");

        if (name.length() < 2 || name.length() > 10)
            throw new IllegalArgumentException("이름은 2자 이상 10자 이하여야 합니다.");

        if (phoneNumber == null || !PHONE_NUMBER_PATTERN.matcher(phoneNumber).matches())
            throw new IllegalArgumentException("올바른 전화번호 형식이 아닙니다. (010XXXXXXXX)");
    }
}
