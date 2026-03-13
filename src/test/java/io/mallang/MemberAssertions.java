package io.mallang;

import io.mallang.member.domain.Email;
import io.mallang.member.domain.Member;
import io.mallang.member.domain.MemberCreateCommand;
import io.mallang.member.domain.Nickname;
import io.mallang.member.domain.PasswordEncoder;
import org.assertj.core.api.ThrowingConsumer;

import static org.assertj.core.api.Assertions.assertThat;

public class MemberAssertions {

    public static ThrowingConsumer<Member> isDerivedFrom(MemberCreateCommand command, PasswordEncoder passwordEncoder) {
        return member -> {
            assertThat(member.getEmail()).isEqualTo(new Email(command.email()));
            assertThat(member.getNickname()).isEqualTo(new Nickname(command.nickname()));
            assertThat(member.verifyPassword(command.password(), passwordEncoder)).isTrue();
        };
    }
}
