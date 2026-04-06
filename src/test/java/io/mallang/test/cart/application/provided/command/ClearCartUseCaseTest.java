package io.mallang.test.cart.application.provided.command;

import io.mallang.annotations.UseCaseTest;
import io.mallang.cart.application.provided.command.ClearCartUseCase;
import io.mallang.cart.application.provided.command.model.ClearCartCommand;
import io.mallang.cart.application.required.command.SaveCartPort;
import io.mallang.cart.application.required.query.LoadCartPort;
import io.mallang.cart.domain.Cart;
import io.mallang.cart.domain.exception.CartNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static io.mallang.fixtures.CartFixture.generateCart;
import static io.mallang.fixtures.CartFixture.generateCartWithItem;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@UseCaseTest
@DisplayName("ClearCart UseCase")
class ClearCartUseCaseTest {

    @Test
    void 장바구니가_없으면_예외가_발생한다(
            @Autowired ClearCartUseCase clearCartUseCase
    ) {
        // given
        var command = new ClearCartCommand("member-1");

        // when & then
        assertThatThrownBy(() -> clearCartUseCase.clear(command))
                .isInstanceOf(CartNotFoundException.class);
    }

    @Test
    void 장바구니의_모든_항목을_비우고_저장한다(
            @Autowired ClearCartUseCase clearCartUseCase,
            @Autowired SaveCartPort saveCartPort,
            @Autowired LoadCartPort loadCartPort
    ) {
        // given
        Cart cart = generateCartWithItem(3);
        saveCartPort.save(cart);

        var command = new ClearCartCommand(cart.getMemberId().value());

        // when
        clearCartUseCase.clear(command);

        // then
        Cart loaded = loadCartPort.getByMemberId(cart.getMemberId());
        assertThat(loaded.getItems()).isEmpty();
    }

    @Test
    void 이미_비어있는_장바구니도_예외_없이_비운다(
            @Autowired ClearCartUseCase clearCartUseCase,
            @Autowired SaveCartPort saveCartPort,
            @Autowired LoadCartPort loadCartPort
    ) {
        // given
        Cart cart = generateCart();
        saveCartPort.save(cart);

        var command = new ClearCartCommand(cart.getMemberId().value());

        // when
        clearCartUseCase.clear(command);

        // then
        Cart loaded = loadCartPort.getByMemberId(cart.getMemberId());
        assertThat(loaded.getItems()).isEmpty();
    }
}
