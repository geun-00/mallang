package io.mallang.test.member.application.required.query;

import io.mallang.fixtures.MemberFixture;
import io.mallang.member.application.required.command.SaveMemberPort;
import io.mallang.member.application.required.query.LoadMemberPort;
import io.mallang.member.domain.Member;
import io.mallang.member.domain.MemberId;
import io.mallang.member.domain.MemberNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class LoadMemberPortTest {

    @Autowired LoadMemberPort loadMemberPort;
    @Autowired SaveMemberPort saveMemberPort;

    @Test
    void 저장된_Member를_조회한다() {
        // given
        Member member = MemberFixture.generateMember();
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
}
