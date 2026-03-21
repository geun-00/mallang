package io.mallang.member.application.provided.command.model;

public record RegisterMemberCommand(String email, String password, String nickname) {
}
