package io.mallang.member.domain.command;

import io.mallang.member.domain.Email;
import io.mallang.member.domain.Nickname;

public record CreateMemberCommand(
        Email email,
        String password,
        Nickname nickname
) {
}
