package io.mallang.member.domain.command;

public record MemberCreateCommand(String email, String password, String nickname) {
}
