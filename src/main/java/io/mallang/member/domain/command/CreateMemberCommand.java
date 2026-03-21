package io.mallang.member.domain.command;

public record CreateMemberCommand(String email, String password, String nickname) {
}
