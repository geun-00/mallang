package io.mallang.test.member.application.required.query;

import io.mallang.annotations.PortTest;
import io.mallang.common.domain.exception.AggregateNotLoadedException;
import io.mallang.fixtures.CommonFixture;
import io.mallang.member.application.required.command.SaveMemberPort;
import io.mallang.member.application.required.query.LoadMemberPort;
import io.mallang.member.domain.Email;
import io.mallang.member.domain.Member;
import io.mallang.member.domain.MemberId;
import io.mallang.member.domain.Nickname;
import io.mallang.member.domain.exception.MemberNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static io.mallang.fixtures.MemberFixture.*;
import static org.assertj.core.api.Assertions.*;

@PortTest
@DisplayName("LoadMember Port")
class LoadMemberPortTest {

    @Nested
    class 조회 {

        @Test
        void 저장된_Member를_조회한다(
                @Autowired LoadMemberPort loadMemberPort,
                @Autowired SaveMemberPort saveMemberPort
        ) {
            // given
            Member member = generateMember();
            saveMemberPort.save(member);

            // when & then
            assertThatCode(() -> loadMemberPort.getById(member.getId()))
                    .doesNotThrowAnyException();
        }

        @Test
        void getById로_조회한_Member는_배송지_관련_기능을_사용할_수_없다(
                @Autowired LoadMemberPort loadMemberPort,
                @Autowired SaveMemberPort saveMemberPort
        ) {
            // given
            Member member = generateMember();
            saveMemberPort.save(member);

            Member loaded = loadMemberPort.getById(member.getId());

            // when & then
            assertThatThrownBy(() -> loaded.addShippingAddress(generateAddShippingAddressCommand(), CommonFixture.generateIdGenerator()))
                    .isInstanceOf(AggregateNotLoadedException.class);
        }

        @Test
        void 존재하지_않는_ID로_조회하면_MemberNotFoundException_예외가_발생한다(
                @Autowired LoadMemberPort loadMemberPort
        ) {
            // given
            MemberId unknownId = new MemberId("unknown");

            // when & then
            assertThatThrownBy(() -> loadMemberPort.getById(unknownId))
                    .isInstanceOf(MemberNotFoundException.class);
        }

        @Test
        void 저장된_이메일로_Member를_조회한다(
                @Autowired LoadMemberPort loadMemberPort,
                @Autowired SaveMemberPort saveMemberPort
        ) {
            // given
            Member member = generateMember();
            saveMemberPort.save(member);

            // when & then
            assertThatCode(() -> loadMemberPort.getByEmail(member.getEmail()))
                    .doesNotThrowAnyException();
        }

        @Test
        void 존재하지_않는_이메일로_조회하면_MemberNotFoundException_예외가_발생한다(
                @Autowired LoadMemberPort loadMemberPort
        ) {
            // given
            Email unknownEmail = new Email(generateEmailValue());

            // when & then
            assertThatThrownBy(() -> loadMemberPort.getByEmail(unknownEmail))
                    .isInstanceOf(MemberNotFoundException.class);
        }

        @Test
        void getByIdWithAddresses로_Member를_조회한다(
                @Autowired LoadMemberPort loadMemberPort,
                @Autowired SaveMemberPort saveMemberPort
        ) {
            // given
            Member member = generateMemberWithShippingAddress();
            saveMemberPort.save(member);

            // when & then
            assertThatCode(() -> loadMemberPort.getByIdWithAddresses(member.getId()))
                    .doesNotThrowAnyException();
        }

        @Test
        void getByIdWithAddresses는_존재하지_않는_ID로_조회하면_MemberNotFoundException_예외가_발생한다(
                @Autowired LoadMemberPort loadMemberPort
        ) {
            // given
            MemberId unknownId = new MemberId("unknown");

            // when & then
            assertThatThrownBy(() -> loadMemberPort.getByIdWithAddresses(unknownId))
                    .isInstanceOf(MemberNotFoundException.class);
        }
    }

    @Nested
    class 존재_여부 {

        @Test
        void 저장된_이메일로_존재_여부를_조회하면_true를_반환한다(
                @Autowired LoadMemberPort loadMemberPort,
                @Autowired SaveMemberPort saveMemberPort
        ) {
            // given
            Member member = generateMember();
            saveMemberPort.save(member);

            // when & then
            assertThat(loadMemberPort.existsByEmail(member.getEmail())).isTrue();
        }

        @Test
        void 저장되지_않은_이메일로_존재_여부를_조회하면_false를_반환한다(
                @Autowired LoadMemberPort loadMemberPort
        ) {
            // given
            Email unknownEmail = new Email(generateEmailValue());

            // when & then
            assertThat(loadMemberPort.existsByEmail(unknownEmail)).isFalse();
        }

        @Test
        void 저장된_닉네임으로_존재_여부를_조회하면_true를_반환한다(
                @Autowired LoadMemberPort loadMemberPort,
                @Autowired SaveMemberPort saveMemberPort
        ) {
            // given
            Member member = generateMember();
            saveMemberPort.save(member);

            // when & then
            assertThat(loadMemberPort.existsByNickname(member.getNickname())).isTrue();
        }

        @Test
        void 저장되지_않은_닉네임으로_존재_여부를_조회하면_false를_반환한다(
                @Autowired LoadMemberPort loadMemberPort
        ) {
            // given
            Nickname unknownNickname = new Nickname(generateNicknameValue());

            // when & then
            assertThat(loadMemberPort.existsByNickname(unknownNickname)).isFalse();
        }
    }
}
