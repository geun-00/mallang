package io.mallang.test.member.application.required.command;

import io.mallang.PortTest;
import io.mallang.assertions.MemberAssertions;
import io.mallang.member.application.required.command.SaveMemberPort;
import io.mallang.member.application.required.query.LoadMemberPort;
import io.mallang.member.domain.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static io.mallang.fixtures.MemberFixture.*;
import static org.assertj.core.api.Assertions.assertThat;

@PortTest
@DisplayName("SaveMember Port")
class SaveMemberPortTest {

    @Test
    void 저장하면_조회된다(
            @Autowired SaveMemberPort saveMemberPort,
            @Autowired LoadMemberPort loadMemberPort
    ) {

        // given
        Member member = generateMember();

        // when
        saveMemberPort.save(member);

        // then
        assertThat(loadMemberPort.getById(member.getId()))
                .isNotNull()
                .satisfies(MemberAssertions.isSameAs(member));
    }

    @Test
    void 저장된_회원을_수정한_뒤_다시_저장하면_변경사항이_반영된다(
            @Autowired SaveMemberPort saveMemberPort,
            @Autowired LoadMemberPort loadMemberPort
    ) {
        // given
        Member member = generateMember();
        saveMemberPort.save(member);

        member.addShippingAddress(generateAddShippingAddressCommand(), generateIdGenerator());
        member.withdraw(generateClockHolder());

        // when
        saveMemberPort.save(member);

        // then
        assertThat(loadMemberPort.getByIdWithAddresses(member.getId()).getShippingAddresses()).hasSize(1);
        assertThat(loadMemberPort.getById(member.getId()).getStatus()).isEqualTo(member.getStatus());
        assertThat(loadMemberPort.getById(member.getId()).getWithdrawnAt()).isEqualTo(member.getWithdrawnAt());
    }
}
