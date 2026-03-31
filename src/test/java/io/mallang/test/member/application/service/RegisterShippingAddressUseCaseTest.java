package io.mallang.test.member.application.service;

import io.mallang.UseCaseTest;
import io.mallang.member.application.provided.command.RegisterShippingAddressUseCase;
import io.mallang.member.application.provided.command.model.RegisterShippingAddressCommand;
import io.mallang.member.application.provided.command.model.RegisterShippingAddressResult;
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

import static io.mallang.fixtures.MemberFixture.savedMemberId;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@UseCaseTest
@DisplayName("RegisterShippingAddress UseCase")
class RegisterShippingAddressUseCaseTest {

    @Test
    void 배송지를_추가하면_반환된_ShippingAddressId가_저장된_회원의_배송지_목록에_포함된다(
            @Autowired SaveMemberPort saveMemberPort,
            @Autowired LoadMemberPort loadMemberPort,
            @Autowired RegisterShippingAddressUseCase registerShippingAddressUseCase
    ) {
        // given
        MemberId memberId = savedMemberId(saveMemberPort);

        // when
        RegisterShippingAddressResult result = registerShippingAddressUseCase.register(command(memberId));

        // then
        Member member = loadMemberPort.getByIdWithAddresses(memberId);
        assertThat(member.getShippingAddresses())
                .hasSize(1)
                .extracting(ShippingAddress::getId)
                .contains(new ShippingAddressId(result.shippingAddressId()));
    }

    @Test
    void 존재하지_않는_회원에게_배송지를_추가하면_MemberNotFoundException이_발생한다(
            @Autowired RegisterShippingAddressUseCase registerShippingAddressUseCase
    ) {
        // given
        MemberId wrongMemberId = new MemberId("wrong-id");

        // when & then
        assertThatThrownBy(() -> registerShippingAddressUseCase.register(command(wrongMemberId)))
                .isInstanceOf(MemberNotFoundException.class);
    }

    private RegisterShippingAddressCommand command(MemberId memberId) {
        return new RegisterShippingAddressCommand(
                memberId.value(),
                "홍길동",
                "01011112222",
                "12345",
                "서울시 강남구 테헤란로 1",
                "101호"
        );
    }
}
