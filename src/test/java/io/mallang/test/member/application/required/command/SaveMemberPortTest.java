package io.mallang.test.member.application.required.command;

import io.mallang.MemberAssertions;
import io.mallang.fixtures.MemberFixture;
import io.mallang.member.application.required.command.SaveMemberPort;
import io.mallang.member.application.required.query.LoadMemberPort;
import io.mallang.member.domain.Member;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class SaveMemberPortTest {

    @Autowired SaveMemberPort saveMemberPort;
    @Autowired LoadMemberPort loadMemberPort;

    @Test
    void 저장하면_조회된다() {
        // given
        Member member = MemberFixture.generateMember();

        // when
        saveMemberPort.save(member);

        // then
        assertThat(loadMemberPort.getById(member.getId()))
                .isNotNull()
                .satisfies(MemberAssertions.isSameAs(member));
    }
}