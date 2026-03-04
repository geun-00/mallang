package io.mallang.member.domain;

public interface PasswordEncoder {
    String encode(String rawPassword);

    boolean matches(String rawPassword, String hashedPassword);
}