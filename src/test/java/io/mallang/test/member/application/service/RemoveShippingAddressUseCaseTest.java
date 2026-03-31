package io.mallang.test.member.application.service;

import io.mallang.UseCaseTest;
import io.mallang.member.application.provided.command.RemoveShippingAddressUseCase;
import io.mallang.member.application.provided.command.model.RemoveShippingAddressCommand;
import io.mallang.member.application.required.command.SaveMemberPort;
import io.mallang.member.application.required.query.LoadMemberPort;
import io.mallang.member.domain.Member;
import io.mallang.member.domain.MemberId;
import io.mallang.member.domain.ShippingAddress;
import io.mallang.member.domain.ShippingAddressId;
import io.mallang.member.domain.exception.MemberNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static io.mallang.fixtures.MemberFixture.savedMemberIdWithShippingAddress;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@UseCaseTest
@DisplayName("RemoveShippingAddress UseCase")
class RemoveShippingAddressUseCaseTest {

    @Test
    void 배송지를_삭제하면_저장된_회원의_배송지_목록에서_제거된다(
            @Autowired SaveMemberPort saveMemberPort,
            @Autowired LoadMemberPort loadMemberPort,
            @Autowired RemoveShippingAddressUseCase removeShippingAddressUseCase
    ) {
        // given
        MemberId memberId = savedMemberIdWithShippingAddress(saveMemberPort);
        ShippingAddressId shippingAddressId = firstShippingAddressId(memberId, loadMemberPort);

        // when
        removeShippingAddressUseCase.remove(command(memberId, shippingAddressId));

        // then
        Member member = loadMemberPort.getByIdWithAddresses(memberId);
        assertThat(member.getShippingAddresses())
                .extracting(ShippingAddress::getId)
                .doesNotContain(shippingAddressId);
    }

    @Test
    void 존재하지_않는_회원의_배송지를_삭제하면_MemberNotFoundException이_발생한다(
            @Autowired RemoveShippingAddressUseCase removeShippingAddressUseCase
    ) {
        // given
        MemberId wrongMemberId = new MemberId("wrong-id");
        ShippingAddressId anyId = new ShippingAddressId("any-id");

        // when & then
        assertThatThrownBy(() -> removeShippingAddressUseCase.remove(command(wrongMemberId, anyId)))
                .isInstanceOf(MemberNotFoundException.class);
    }

    private ShippingAddressId firstShippingAddressId(MemberId memberId, LoadMemberPort loadMemberPort) {
        return loadMemberPort.getByIdWithAddresses(memberId).getShippingAddresses().getFirst().getId();
    }

    private RemoveShippingAddressCommand command(MemberId memberId, ShippingAddressId shippingAddressId) {
        return new RemoveShippingAddressCommand(memberId.value(), shippingAddressId.value());
    }
}
