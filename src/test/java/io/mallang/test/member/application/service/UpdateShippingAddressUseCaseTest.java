package io.mallang.test.member.application.service;

import io.mallang.UseCaseTest;
import io.mallang.member.application.provided.command.UpdateShippingAddressUseCase;
import io.mallang.member.application.provided.command.model.UpdateShippingAddressCommand;
import io.mallang.member.application.required.command.SaveMemberPort;
import io.mallang.member.application.required.query.LoadMemberPort;
import io.mallang.member.domain.MemberId;
import io.mallang.member.domain.ShippingAddressId;
import io.mallang.member.domain.exception.MemberNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static io.mallang.assertions.MemberAssertions.isModifiedBy;
import static io.mallang.fixtures.MemberFixture.savedMemberIdWithShippingAddress;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@UseCaseTest
@DisplayName("UpdateShippingAddress UseCase")
class UpdateShippingAddressUseCaseTest {

    @Test
    void 배송지를_수정하면_저장된_회원의_배송지_정보가_변경된다(
            @Autowired SaveMemberPort saveMemberPort,
            @Autowired LoadMemberPort loadMemberPort,
            @Autowired UpdateShippingAddressUseCase updateShippingAddressUseCase
    ) {
        // given
        MemberId memberId = savedMemberIdWithShippingAddress(saveMemberPort);
        ShippingAddressId shippingAddressId = firstShippingAddressId(memberId, loadMemberPort);
        UpdateShippingAddressCommand command = command(memberId, shippingAddressId);

        // when
        updateShippingAddressUseCase.update(command);

        // then
        assertThat(loadMemberPort.getByIdWithAddresses(memberId).getShippingAddresses().getFirst())
                .satisfies(isModifiedBy(command));
    }

    @Test
    void 존재하지_않는_회원의_배송지를_수정하면_MemberNotFoundException이_발생한다(
            @Autowired UpdateShippingAddressUseCase updateShippingAddressUseCase
    ) {
        // given
        MemberId wrongMemberId = new MemberId("wrong-id");
        ShippingAddressId anyId = new ShippingAddressId("any-id");

        // when & then
        assertThatThrownBy(() -> updateShippingAddressUseCase.update(command(wrongMemberId, anyId)))
                .isInstanceOf(MemberNotFoundException.class);
    }

    private ShippingAddressId firstShippingAddressId(MemberId memberId, LoadMemberPort loadMemberPort) {
        return loadMemberPort.getByIdWithAddresses(memberId).getShippingAddresses().getFirst().getId();
    }

    private UpdateShippingAddressCommand command(MemberId memberId, ShippingAddressId shippingAddressId) {
        return new UpdateShippingAddressCommand(
                memberId.value(),
                shippingAddressId.value(),
                "이순신",
                "01099998888",
                "99999",
                "부산시 해운대구 해운대로 1",
                "202호"
        );
    }
}
