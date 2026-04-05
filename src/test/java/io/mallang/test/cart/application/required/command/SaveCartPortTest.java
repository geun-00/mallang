package io.mallang.test.cart.application.required.command;

import io.mallang.TestConfig;
import io.mallang.cart.application.required.command.SaveCartPort;
import io.mallang.cart.application.required.query.LoadCartPort;
import io.mallang.cart.domain.command.AddCartItemCommand;
import io.mallang.cart.domain.Cart;
import io.mallang.cart.domain.CartItemId;
import io.mallang.product.domain.ProductId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import static io.mallang.assertions.CartAssertions.isSameAs;
import static io.mallang.fixtures.CartFixture.generateIdGenerator;
import static io.mallang.fixtures.CartFixture.generateCartWithItem;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Import(TestConfig.class)
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
        assertThat(loaded).satisfies(isSameAs(cart));
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
        cart.addItem(new AddCartItemCommand(new ProductId("product-1"), 3), generateIdGenerator());

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
