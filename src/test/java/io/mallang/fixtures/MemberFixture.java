package io.mallang.fixtures;

import io.mallang.domain.common.IdGenerator;
import io.mallang.member.domain.Member;
import io.mallang.member.domain.MemberCreateCommand;
import io.mallang.member.domain.PasswordEncoder;

import java.util.UUID;

public class MemberFixture {

    public static PasswordEncoder generatePasswordEncoder() {
        return new PasswordEncoder() {
            @Override
            public String encode(String rawPassword) {
                return rawPassword.toUpperCase() + "encoded";
            }

            @Override
            public boolean matches(String rawPassword, String hashedPassword) {
                return encode(rawPassword).equals(hashedPassword);
            }
        };
    }

    public static IdGenerator generateIdGenerator() {
        return () -> UUID.randomUUID().toString();
    }

    public static MemberCreateCommand generateCreateCommand() {
        return generateCreateCommand("password12@");
    }

    public static MemberCreateCommand generateCreateCommand(String password) {
        return new MemberCreateCommand(
                UUID.randomUUID() + "@test.com",
                password,
                UUID.randomUUID().toString().substring(0, 20)
        );
    }

    public static Member generateMember() {
        return Member.create(generateCreateCommand(), generatePasswordEncoder(), generateIdGenerator());
    }

    public static Member generateMember(String password) {
        return Member.create(generateCreateCommand(password), generatePasswordEncoder(), generateIdGenerator());
    }
}
