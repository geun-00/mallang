package io.mallang.member.domain;

import java.util.regex.Pattern;

public record Password(String value) {

    private static final Pattern LETTER_PATTERN = Pattern.compile("[a-zA-Z]");
    private static final Pattern DIGIT_PATTERN = Pattern.compile("[0-9]");
    private static final Pattern SPECIAL_CHAR_PATTERN = Pattern.compile("[!@#$%^&*]");
    private static final Pattern ALLOWED_CHARS_PATTERN = Pattern.compile("^[a-zA-Z0-9!@#$%^&*]+$");

    static Password encode(String rawPassword, PasswordEncoder passwordEncoder) {
        rawPassword = rawPassword.strip();

        validatePassword(rawPassword);

        return new Password(passwordEncoder.encode(rawPassword));
    }

    private static void validatePassword(String rawPassword) {
        if (rawPassword.length() < 8 || rawPassword.length() > 20)
            throw new IllegalArgumentException("비밀번호는 8자 이상 20자 이하여야 합니다.");
        if (!LETTER_PATTERN.matcher(rawPassword).find())
            throw new IllegalArgumentException("비밀번호에 영문자를 포함해야 합니다.");
        if (!DIGIT_PATTERN.matcher(rawPassword).find())
            throw new IllegalArgumentException("비밀번호에 숫자를 포함해야 합니다.");
        if (!SPECIAL_CHAR_PATTERN.matcher(rawPassword).find())
            throw new IllegalArgumentException("비밀번호에 특수문자(!@#$%^&*)를 포함해야 합니다.");
        if (!ALLOWED_CHARS_PATTERN.matcher(rawPassword).matches())
            throw new IllegalArgumentException("비밀번호에 허용되지 않은 문자가 포함되어 있습니다.");
    }

    boolean verifyPassword(String rawPassword, PasswordEncoder passwordEncoder) {
        return passwordEncoder.matches(rawPassword, this.value);
    }
}
