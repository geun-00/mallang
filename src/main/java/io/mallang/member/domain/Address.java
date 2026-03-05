package io.mallang.member.domain;

import java.util.regex.Pattern;

public record Address(String zipCode, String mainAddress, String detailAddress) {

    private static final Pattern ZIP_CODE_PATTERN = Pattern.compile("\\d{5}");

    public Address {
        if (zipCode == null || !ZIP_CODE_PATTERN.matcher(zipCode).matches())
            throw new IllegalArgumentException("우편번호는 5자리 숫자여야 합니다.");

        if (mainAddress == null || mainAddress.isBlank())
            throw new IllegalArgumentException("주소를 입력해주세요.");
    }
}
