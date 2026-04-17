package io.mallang.fixtures;

import io.mallang.common.domain.vo.Address;
import io.mallang.common.domain.vo.Receiver;
import io.mallang.member.adapter.web.model.MemberCreateRequest;
import io.mallang.member.adapter.web.model.RegisterShippingAddressRequest;
import io.mallang.member.adapter.web.model.UpdateShippingAddressRequest;
import io.mallang.member.application.provided.command.model.RegisterMemberCommand;
import io.mallang.member.application.required.command.SaveMemberPort;
import io.mallang.member.domain.*;
import io.mallang.member.domain.command.AddShippingAddressCommand;
import io.mallang.member.domain.command.CreateMemberCommand;
import io.mallang.member.domain.command.ModifyShippingAddressCommand;

import java.util.UUID;

public class MemberFixture {

    // =====================================================================
    // 테스트 상수 (Test Constants)
    // =====================================================================

    public static final String DEFAULT_PASSWORD = "password12@";

    // =====================================================================
    // 테스트 도구 (Test Utilities)
    // =====================================================================

    public static MemberPasswordEncoder generatePasswordEncoder() {
        return new MemberPasswordEncoder() {
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

    // =====================================================================
    // 도메인 커맨드 (Domain Commands)
    // =====================================================================

    public static CreateMemberCommand generateCreateCommand() {
        return generateCreateCommand(DEFAULT_PASSWORD);
    }

    public static CreateMemberCommand generateCreateCommand(String password) {
        return new CreateMemberCommand(
                new Email(generateEmailValue()),
                password,
                new Nickname(generateNicknameValue())
        );
    }

    // =====================================================================
    // 애플리케이션 커맨드 (Application Commands)
    // =====================================================================

    public static RegisterMemberCommand generateRegisterCommand() {
        return generateRegisterCommand(DEFAULT_PASSWORD);
    }

    public static RegisterMemberCommand generateRegisterCommand(String password) {
        return new RegisterMemberCommand(
                generateEmailValue(),
                password,
                generateNicknameValue()
        );
    }

    // =====================================================================
    // 랜덤 원시값 (Random Primitives)
    // =====================================================================

    public static String generateEmailValue() {
        return UUID.randomUUID() + "@test.com";
    }

    public static String generateNicknameValue() {
        return UUID.randomUUID().toString().substring(0, 20);
    }

    public static MemberId generateMemberId() {
        return new MemberId(UUID.randomUUID().toString());
    }

    // =====================================================================
    // 웹 요청 모델 (Web Request Models)
    // =====================================================================

    public static MemberCreateRequest generateCreateRequest() {
        return new MemberCreateRequest(generateEmailValue(), DEFAULT_PASSWORD, generateNicknameValue());
    }

    public static MemberCreateRequest generateCreateRequest(String email) {
        return new MemberCreateRequest(email, DEFAULT_PASSWORD, generateNicknameValue());
    }

    public static RegisterShippingAddressRequest generateRegisterShippingAddressRequest() {
        return new RegisterShippingAddressRequest(
                "홍길동",
                "01011112222",
                "12345",
                "서울시 강남구 테헤란로 1",
                "101호"
        );
    }

    public static UpdateShippingAddressRequest generateUpdateShippingAddressRequest() {
        return new UpdateShippingAddressRequest(
                "이순신",
                "01022223333",
                "13579",
                "경기도 부천시 원미구",
                "2층"
        );
    }

    // =====================================================================
    // 도메인 객체 (Domain Objects)
    // =====================================================================

    public static Member generateMember() {
        return Member.create(generateCreateCommand(), generatePasswordEncoder(), CommonFixture.generateIdGenerator(), CommonFixture.generateClockHolder());
    }

    public static Member generateMemberWithNickname(String nickname) {
        return Member.create(
                new CreateMemberCommand(
                        new Email(generateEmailValue()),
                        DEFAULT_PASSWORD,
                        new Nickname(nickname)
                ),
                generatePasswordEncoder(),
                CommonFixture.generateIdGenerator(),
                CommonFixture.generateClockHolder()
        );
    }

    public static Member generateMember(String password) {
        return Member.create(generateCreateCommand(password), generatePasswordEncoder(), CommonFixture.generateIdGenerator(), CommonFixture.generateClockHolder());
    }

    // =====================================================================
    // 배송지 도메인 커맨드 (Shipping Address Domain Commands)
    // =====================================================================

    public static AddShippingAddressCommand generateAddShippingAddressCommand() {
        return new AddShippingAddressCommand(
                new Receiver("홍길동", "01011112222"),
                new Address("12345", "서울시 강남구 테헤란로 1", "101호")
        );
    }

    public static ModifyShippingAddressCommand generateModifyShippingAddressCommand() {
        return new ModifyShippingAddressCommand(
                new Receiver("이순신", "01022223333"),
                new Address("13579", "경기도 부천시 원미구", "2층")
        );
    }

    public static Member generateMemberWithShippingAddress() {
        return generateMemberWithShippingAddress(1);
    }

    public static Member generateMemberWithShippingAddress(int count) {
        Member member = generateMember();
        for (int i = 0; i < count; i++) {
            member.addShippingAddress(generateAddShippingAddressCommand(), CommonFixture.generateIdGenerator());
        }
        return member;
    }

    public static Member generateWithdrawnMember() {
        Member member = generateMember();
        member.withdraw(CommonFixture.generateClockHolder());
        return member;
    }

    public static Member generateWithdrawnMemberWithShippingAddress() {
        Member member = generateMemberWithShippingAddress();
        member.withdraw(CommonFixture.generateClockHolder());
        return member;
    }

    // =====================================================================
    // 저장된 도메인 식별자 (Saved Domain Identifiers)
    // =====================================================================

    public static MemberId savedMemberId(SaveMemberPort saveMemberPort) {
        Member member = generateMember();
        saveMemberPort.save(member);

        return member.getId();
    }

    public static MemberId savedMemberIdWithShippingAddress(SaveMemberPort saveMemberPort) {
        return savedMemberIdWithShippingAddress(saveMemberPort, 1);
    }

    public static MemberId savedMemberIdWithShippingAddress(SaveMemberPort saveMemberPort, int count) {
        Member member = generateMemberWithShippingAddress(count);
        saveMemberPort.save(member);

        return member.getId();
    }
}
