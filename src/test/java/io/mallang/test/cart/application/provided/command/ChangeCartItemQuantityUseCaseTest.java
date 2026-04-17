package io.mallang.test.cart.application.provided.command;

import io.mallang.annotations.UseCaseTest;
import io.mallang.cart.application.provided.command.ChangeCartItemQuantityUseCase;
import io.mallang.cart.application.provided.command.model.ChangeCartItemQuantityCommand;
import io.mallang.cart.application.required.command.SaveCartPort;
import io.mallang.cart.application.required.query.LoadCartPort;
import io.mallang.cart.domain.Cart;
import io.mallang.cart.domain.CartItem;
import io.mallang.cart.domain.CartItemId;
import io.mallang.cart.domain.command.AddCartItemCommand;
import io.mallang.cart.domain.exception.CartItemNotFoundException;
import io.mallang.cart.domain.exception.CartNotFoundException;
import io.mallang.common.domain.exception.InvalidValueException;
import io.mallang.product.application.required.command.SaveProductPort;
import io.mallang.product.domain.Product;
import io.mallang.product.domain.exception.ProductNotFoundException;
import io.mallang.stock.application.required.command.SaveStockPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import static io.mallang.fixtures.CartFixture.generateCart;
import static io.mallang.fixtures.CommonFixture.generateIdGenerator;
import static io.mallang.fixtures.ProductFixture.generateProduct;
import static io.mallang.fixtures.ProductFixture.generateProductId;
import static io.mallang.fixtures.StockFixture.generateStock;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@UseCaseTest
@DisplayName("ChangeCartItemQuantity UseCase")
class ChangeCartItemQuantityUseCaseTest {

    @Test
    void 장바구니가_없으면_예외가_발생한다(
            @Autowired ChangeCartItemQuantityUseCase changeCartItemQuantityUseCase
    ) {
        // given
        var command = new ChangeCartItemQuantityCommand(
                "member-" + UUID.randomUUID(),
                "cart-item-" + UUID.randomUUID(),
                2
        );

        // when & then
        assertThatThrownBy(() -> changeCartItemQuantityUseCase.changeQuantity(command))
                .isInstanceOf(CartNotFoundException.class);
    }

    @Test
    void 존재하지_않는_장바구니_항목이면_예외가_발생한다(
            @Autowired ChangeCartItemQuantityUseCase changeCartItemQuantityUseCase,
            @Autowired SaveCartPort saveCartPort
    ) {
        // given
        Cart cart = generateCart();
        saveCartPort.save(cart);

        var command = new ChangeCartItemQuantityCommand(
                cart.getMemberId().value(),
                "cart-item-" + UUID.randomUUID(),
                2
        );

        // when & then
        assertThatThrownBy(() -> changeCartItemQuantityUseCase.changeQuantity(command))
                .isInstanceOf(CartItemNotFoundException.class);
    }

    @Test
    void 존재하지_않는_상품이면_예외가_발생한다(
            @Autowired ChangeCartItemQuantityUseCase changeCartItemQuantityUseCase,
            @Autowired SaveCartPort saveCartPort
    ) {
        // given
        Cart cart = generateCart();
        CartItemId cartItemId = cart.addItem(
                new AddCartItemCommand(generateProductId(), 2),
                generateIdGenerator()
        );
        saveCartPort.save(cart);

        var command = new ChangeCartItemQuantityCommand(
                cart.getMemberId().value(),
                cartItemId.value(),
                3
        );

        // when & then
        assertThatThrownBy(() -> changeCartItemQuantityUseCase.changeQuantity(command))
                .isInstanceOf(ProductNotFoundException.class);
    }

    @Test
    void 재고보다_많은_수량으로_변경할_수_없다(
            @Autowired ChangeCartItemQuantityUseCase changeCartItemQuantityUseCase,
            @Autowired SaveCartPort saveCartPort,
            @Autowired SaveProductPort saveProductPort,
            @Autowired SaveStockPort saveStockPort
    ) {
        // given
        Cart cart = generateCart();
        Product product = generateProduct();
        CartItemId cartItemId = cart.addItem(
                new AddCartItemCommand(product.getId(), 2),
                generateIdGenerator()
        );

        saveCartPort.save(cart);
        saveProductPort.save(product);
        saveStockPort.save(generateStock(product, 4));

        var command = new ChangeCartItemQuantityCommand(
                cart.getMemberId().value(),
                cartItemId.value(),
                5
        );

        // when & then
        assertThatThrownBy(() -> changeCartItemQuantityUseCase.changeQuantity(command))
                .isInstanceOf(InvalidValueException.class);
    }

    @Test
    void 장바구니_항목의_수량을_변경하고_저장한다(
            @Autowired ChangeCartItemQuantityUseCase changeCartItemQuantityUseCase,
            @Autowired SaveCartPort saveCartPort,
            @Autowired LoadCartPort loadCartPort,
            @Autowired SaveProductPort saveProductPort,
            @Autowired SaveStockPort saveStockPort
    ) {
        // given
        Cart cart = generateCart();
        Product product = generateProduct();
        CartItemId cartItemId = cart.addItem(
                new AddCartItemCommand(product.getId(), 2),
                generateIdGenerator()
        );

        saveCartPort.save(cart);
        saveProductPort.save(product);
        saveStockPort.save(generateStock(product, 10));

        var command = new ChangeCartItemQuantityCommand(
                cart.getMemberId().value(),
                cartItemId.value(),
                7
        );

        // when
        changeCartItemQuantityUseCase.changeQuantity(command);

        // then
        Cart loaded = loadCartPort.getByMemberId(cart.getMemberId());
        assertThat(loaded.getItems())
                .hasSize(1)
                .extracting(CartItem::getQuantity)
                .containsExactly(7);
    }
}
