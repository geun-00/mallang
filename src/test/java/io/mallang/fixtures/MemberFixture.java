package io.mallang.fixtures;

import io.mallang.domain.common.ClockHolder;
import io.mallang.domain.common.IdGenerator;
import io.mallang.member.domain.*;

import java.time.LocalDateTime;
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
                return (rawPassword.toUpperCase() + "encoded").equals(hashedPassword);
            }
        };
    }

    public static IdGenerator generateIdGenerator() {
        return () -> UUID.randomUUID().toString();
    }

    public static ClockHolder generateClockHolder() {
        return () -> LocalDateTime.of(2024, 1, 1, 0, 0, 0);
    }

    public static MemberCreateCommand generateCreateCommand() {
        return generateCreateCommand("password12@");
    }

    public static MemberCreateCommand generateCreateCommand(String password) {
        return new MemberCreateCommand(
                generateEmailValue(),
                password,
                generateNicknameValue()
        );
    }

    public static String generateEmailValue() {
        return UUID.randomUUID() + "@test.com";
    }

    public static String generateNicknameValue() {
        return UUID.randomUUID().toString().substring(0, 20);
    }

    public static Member generateMember() {
        return Member.create(generateCreateCommand(), generatePasswordEncoder(), generateIdGenerator(), generateClockHolder());
    }

    public static Member generateMember(String password) {
        return Member.create(generateCreateCommand(password), generatePasswordEncoder(), generateIdGenerator(), generateClockHolder());
    }

    public static AddShippingAddressCommand generateAddShippingAddressCommand() {
        return new AddShippingAddressCommand(
                "홍길동",
                "01011112222",
                "12345",
                "서울시 강남구 테헤란로 1",
                "101호"
        );
    }

    public static ModifyShippingAddressCommand generateModifyShippingAddressCommand() {
        return new ModifyShippingAddressCommand(
                "이순신",
                "01022223333",
                "13579",
                "경기도 부천시 원미구",
                "2층"
        );
    }

    public static Member generateMemberWithShippingAddress() {
        return generateMemberWithShippingAddress(1);
    }

    public static Member generateMemberWithShippingAddress(int count) {
        Member member = generateMember();
        for (int i = 0; i < count; i++) {
            member.addShippingAddress(generateAddShippingAddressCommand(), generateIdGenerator());
        }
        return member;
    }

    public static Member generateWithdrawnMember() {
        Member member = generateMember();
        member.withdraw(generateClockHolder());
        return member;
    }

    public static Member generateWithdrawnMemberWithShippingAddress() {
        Member member = generateMemberWithShippingAddress();
        member.withdraw(generateClockHolder());
        return member;
    }
}
