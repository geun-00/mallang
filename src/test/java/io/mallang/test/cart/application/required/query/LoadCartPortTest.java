package io.mallang.test.cart.application.required.query;

import io.mallang.PortTest;
import io.mallang.cart.application.required.command.SaveCartPort;
import io.mallang.cart.application.required.query.LoadCartPort;
import io.mallang.cart.domain.Cart;
import io.mallang.cart.domain.exception.CartNotFoundException;
import io.mallang.member.domain.MemberId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static io.mallang.assertions.CartAssertions.isSameAs;
import static io.mallang.fixtures.CartFixture.generateCartWithItem;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@PortTest
@DisplayName("LoadCart Port")
class LoadCartPortTest {

    @Test
    void 저장된_장바구니를_조회할_수_있다(
            @Autowired SaveCartPort saveCartPort,
            @Autowired LoadCartPort loadCartPort
    ) {
        // given
        Cart cart = generateCartWithItem(2);
        saveCartPort.save(cart);

        // when
        Cart loaded = loadCartPort.getByMemberId(cart.getMemberId());

        // then
        assertThat(loaded).satisfies(isSameAs(cart));
    }

    @Test
    void 존재하지_않는_장바구니면_예외가_발생한다(
            @Autowired LoadCartPort loadCartPort
    ) {
        // given
        MemberId memberId = new MemberId("unknown-member-id");

        // when & then
        assertThatThrownBy(() -> loadCartPort.getByMemberId(memberId))
                .isInstanceOf(CartNotFoundException.class);
    }
}
