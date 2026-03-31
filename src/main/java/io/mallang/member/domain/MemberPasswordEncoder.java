package io.mallang.member.domain;

public interface MemberPasswordEncoder {

    String encode(String rawPassword);

    boolean matches(String rawPassword, String hashedPassword);
}