package io.mallang.test.cart.application.required.command;

import io.mallang.annotations.PortTest;
import io.mallang.cart.application.required.command.SaveCartPort;
import io.mallang.cart.application.required.query.LoadCartPort;
import io.mallang.cart.domain.Cart;
import io.mallang.cart.domain.CartItemId;
import io.mallang.cart.domain.command.AddCartItemCommand;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static io.mallang.assertions.CartAssertions.isSameAs;
import static io.mallang.fixtures.CartFixture.generateCartWithItem;
import static io.mallang.fixtures.CommonFixture.generateIdGenerator;
import static io.mallang.fixtures.ProductFixture.generateProductId;
import static org.assertj.core.api.Assertions.assertThat;

@PortTest
@DisplayName("SaveCart Port")
class SaveCartPortTest {

    @Test
    void 저장하면_다시_조회할_수_있다(
            @Autowired SaveCartPort saveCartPort,
            @Autowired LoadCartPort loadCartPort
    ) {
        // given
        Cart cart = generateCartWithItem(2);

        // when
        saveCartPort.save(cart);

        // then
        Cart loaded = loadCartPort.getByMemberId(cart.getMemberId());
        assertThat(loaded).isNotNull()
                          .satisfies(isSameAs(cart));
    }

    @Test
    void 기존_장바구니를_수정한_뒤_저장하면_변경사항이_반영된다(
            @Autowired SaveCartPort saveCartPort,
            @Autowired LoadCartPort loadCartPort
    ) {
        // given
        Cart cart = generateCartWithItem(2);
        saveCartPort.save(cart);

        cart.clear();
        cart.addItem(new AddCartItemCommand(
                generateProductId(),
                3
        ), generateIdGenerator());

        // when
        saveCartPort.save(cart);

        // then
        Cart loaded = loadCartPort.getByMemberId(cart.getMemberId());
        assertThat(loaded).satisfies(isSameAs(cart));
    }

    @Test
    void 기존_장바구니_항목의_수량을_변경한_뒤_저장하면_변경사항이_반영된다(
            @Autowired SaveCartPort saveCartPort,
            @Autowired LoadCartPort loadCartPort
    ) {
        // given
        Cart cart = generateCartWithItem(1);
        saveCartPort.save(cart);

        CartItemId itemId = cart.getItems().getFirst().getId();
        cart.changeQuantity(itemId, 5);

        // when
        saveCartPort.save(cart);

        // then
        Cart loaded = loadCartPort.getByMemberId(cart.getMemberId());
        assertThat(loaded).satisfies(isSameAs(cart));
    }
}
