package io.mallang.test.cart.application.provided.command;

import io.mallang.annotations.UseCaseTest;
import io.mallang.cart.application.provided.command.RemoveCartItemUseCase;
import io.mallang.cart.application.provided.command.model.RemoveCartItemCommand;
import io.mallang.cart.application.required.command.SaveCartPort;
import io.mallang.cart.application.required.query.LoadCartPort;
import io.mallang.cart.domain.Cart;
import io.mallang.cart.domain.CartItem;
import io.mallang.cart.domain.CartItemId;
import io.mallang.cart.domain.command.AddCartItemCommand;
import io.mallang.cart.domain.exception.CartItemNotFoundException;
import io.mallang.cart.domain.exception.CartNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import static io.mallang.fixtures.CartFixture.generateCart;
import static io.mallang.fixtures.CommonFixture.generateIdGenerator;
import static io.mallang.fixtures.ProductFixture.generateProductId;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@UseCaseTest
@DisplayName("RemoveCartItem UseCase")
class RemoveCartItemUseCaseTest {

    @Test
    void 장바구니가_없으면_예외가_발생한다(
            @Autowired RemoveCartItemUseCase removeCartItemUseCase
    ) {
        // given
        var command = new RemoveCartItemCommand(
                "member-" + UUID.randomUUID(),
                "cart-item-" + UUID.randomUUID()
        );

        // when & then
        assertThatThrownBy(() -> removeCartItemUseCase.removeItem(command))
                .isInstanceOf(CartNotFoundException.class);
    }

    @Test
    void 존재하지_않는_장바구니_항목이면_예외가_발생한다(
            @Autowired RemoveCartItemUseCase removeCartItemUseCase,
            @Autowired SaveCartPort saveCartPort
    ) {
        // given
        Cart cart = generateCart();
        saveCartPort.save(cart);

        var command = new RemoveCartItemCommand(
                cart.getMemberId().value(),
                "cart-item-" + UUID.randomUUID()
        );

        // when & then
        assertThatThrownBy(() -> removeCartItemUseCase.removeItem(command))
                .isInstanceOf(CartItemNotFoundException.class);
    }

    @Test
    void 장바구니_항목을_제거하고_저장한다(
            @Autowired RemoveCartItemUseCase removeCartItemUseCase,
            @Autowired SaveCartPort saveCartPort,
            @Autowired LoadCartPort loadCartPort
    ) {
        // given
        Cart cart = generateCart();
        CartItemId firstItemId = cart.addItem(
                new AddCartItemCommand(generateProductId(), 1),
                generateIdGenerator()
        );
        CartItemId secondItemId = cart.addItem(
                new AddCartItemCommand(generateProductId(), 2),
                generateIdGenerator()
        );
        saveCartPort.save(cart);

        var command = new RemoveCartItemCommand(
                cart.getMemberId().value(),
                firstItemId.value()
        );

        // when
        removeCartItemUseCase.removeItem(command);

        // then
        Cart loaded = loadCartPort.getByMemberId(cart.getMemberId());
        assertThat(loaded.getItems())
                .hasSize(1)
                .extracting(CartItem::getId)
                .containsExactly(secondItemId);
    }
}
