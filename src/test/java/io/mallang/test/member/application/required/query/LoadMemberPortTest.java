package io.mallang.test.member.application.required.query;

import io.mallang.member.application.required.command.SaveMemberPort;
import io.mallang.member.application.required.query.LoadMemberPort;
import io.mallang.member.domain.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static io.mallang.fixtures.MemberFixture.generateEmailValue;
import static io.mallang.fixtures.MemberFixture.generateMember;
import static io.mallang.fixtures.MemberFixture.generateNicknameValue;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class LoadMemberPortTest {

    @Autowired LoadMemberPort loadMemberPort;
    @Autowired SaveMemberPort saveMemberPort;

    @Test
    void 저장된_Member를_조회한다() {
        // given
        Member member = generateMember();
        saveMemberPort.save(member);

        // when & then
        assertThatCode(() -> loadMemberPort.getById(member.getId()))
                .doesNotThrowAnyException();
    }

    @Test
    void 존재하지_않는_ID로_조회하면_MemberNotFoundException_예외가_발생한다() {
        // given
        MemberId unknownId = new MemberId("unknown");

        // when & then
        assertThatThrownBy(() -> loadMemberPort.getById(unknownId))
                .isInstanceOf(MemberNotFoundException.class);
    }

    @Test
    void 저장된_이메일로_존재_여부를_조회하면_true를_반환한다() {
        // given
        Member member = generateMember();
        saveMemberPort.save(member);

        // when & then
        assertThat(loadMemberPort.existsByEmail(member.getEmail())).isTrue();
    }

    @Test
    void 저장되지_않은_이메일로_존재_여부를_조회하면_false를_반환한다() {
        // given
        Email unknownEmail = new Email(generateEmailValue());

        // when & then
        assertThat(loadMemberPort.existsByEmail(unknownEmail)).isFalse();
    }

    @Test
    void 저장된_닉네임으로_존재_여부를_조회하면_true를_반환한다() {
        // given
        Member member = generateMember();
        saveMemberPort.save(member);

        // when & then
        assertThat(loadMemberPort.existsByNickname(member.getNickname())).isTrue();
    }

    @Test
    void 저장되지_않은_닉네임으로_존재_여부를_조회하면_false를_반환한다() {
        // given
        Nickname unknownNickname = new Nickname(generateNicknameValue());

        // when & then
        assertThat(loadMemberPort.existsByNickname(unknownNickname)).isFalse();
    }
}
