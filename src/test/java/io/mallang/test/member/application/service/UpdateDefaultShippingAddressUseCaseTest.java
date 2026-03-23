package io.mallang.test.member.application.service;

import io.mallang.TestConfig;
import io.mallang.member.application.provided.command.UpdateDefaultShippingAddressUseCase;
import io.mallang.member.application.provided.command.model.UpdateDefaultShippingAddressCommand;
import io.mallang.member.application.required.command.SaveMemberPort;
import io.mallang.member.application.required.query.LoadMemberPort;
import io.mallang.member.domain.Member;
import io.mallang.member.domain.MemberId;
import io.mallang.member.domain.ShippingAddress;
import io.mallang.member.domain.ShippingAddressId;
import io.mallang.member.domain.exception.MemberNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import static io.mallang.fixtures.MemberFixture.generateMemberWithShippingAddress;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Import(TestConfig.class)
class UpdateDefaultShippingAddressUseCaseTest {

    @Test
    void 기본_배송지로_변경하면_저장된_회원의_해당_배송지가_기본_배송지가_된다(
            @Autowired SaveMemberPort saveMemberPort,
            @Autowired LoadMemberPort loadMemberPort,
            @Autowired UpdateDefaultShippingAddressUseCase updateDefaultShippingAddressUseCase
    ) {
        // given
        MemberId memberId = savedMemberIdWithShippingAddresses(2, saveMemberPort);
        ShippingAddressId targetId = secondShippingAddressId(memberId, loadMemberPort);

        // when
        updateDefaultShippingAddressUseCase.update(createUpdateCommand(memberId, targetId));

        // then
        Member member = loadMemberPort.getById(memberId);
        assertThat(member.getShippingAddresses())
                .filteredOn(ShippingAddress::isDefault)
                .extracting(ShippingAddress::getId)
                .containsExactly(targetId);
    }

    @Test
    void 존재하지_않는_회원의_기본_배송지를_변경하면_MemberNotFoundException이_발생한다(
            @Autowired UpdateDefaultShippingAddressUseCase updateDefaultShippingAddressUseCase
    ) {
        // given
        MemberId wrongMemberId = new MemberId("wrong-id");
        ShippingAddressId anyId = new ShippingAddressId("any-id");

        // when & then
        assertThatThrownBy(() -> updateDefaultShippingAddressUseCase.update(createUpdateCommand(wrongMemberId, anyId)))
                .isInstanceOf(MemberNotFoundException.class);
    }

    private MemberId savedMemberIdWithShippingAddresses(int count, SaveMemberPort saveMemberPort) {
        Member member = generateMemberWithShippingAddress(count);
        saveMemberPort.save(member);
        return member.getId();
    }

    private ShippingAddressId secondShippingAddressId(MemberId memberId, LoadMemberPort loadMemberPort) {
        return loadMemberPort.getById(memberId).getShippingAddresses().get(1).getId();
    }

    private UpdateDefaultShippingAddressCommand createUpdateCommand(MemberId memberId, ShippingAddressId shippingAddressId) {
        return new UpdateDefaultShippingAddressCommand(memberId, shippingAddressId);
    }
}
