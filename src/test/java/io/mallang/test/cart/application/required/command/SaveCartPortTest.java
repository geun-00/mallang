package io.mallang.test.cart.application.required.command;

import io.mallang.TestConfig;
import io.mallang.cart.application.required.command.SaveCartPort;
import io.mallang.cart.application.required.query.LoadCartPort;
import io.mallang.cart.domain.Cart;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import static io.mallang.CartAssertions.isSameAs;
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
}
