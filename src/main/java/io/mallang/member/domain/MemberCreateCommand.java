package io.mallang.member.domain;

public record MemberCreateCommand(String email, String password, String nickname) {
}
