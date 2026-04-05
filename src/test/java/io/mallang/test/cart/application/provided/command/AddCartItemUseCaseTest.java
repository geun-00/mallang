package io.mallang.test.cart.application.provided.command;

import io.mallang.TestConfig;
import io.mallang.cart.application.provided.command.AddCartItemUseCase;
import io.mallang.cart.application.provided.command.model.AddItemToCartCommand;
import io.mallang.cart.application.provided.command.model.AddItemToCartResult;
import io.mallang.cart.application.required.command.SaveCartPort;
import io.mallang.cart.application.required.query.LoadCartPort;
import io.mallang.cart.domain.command.AddCartItemCommand;
import io.mallang.cart.domain.Cart;
import io.mallang.cart.domain.CartItem;
import io.mallang.cart.domain.exception.CartNotFoundException;
import io.mallang.domain.common.exception.InvalidValueException;
import io.mallang.member.domain.MemberId;
import io.mallang.product.application.required.command.SaveProductPort;
import io.mallang.product.domain.Product;
import io.mallang.product.domain.exception.ProductNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import static io.mallang.fixtures.CartFixture.generateAddItemToCartCommand;
import static io.mallang.fixtures.CartFixture.generateCart;
import static io.mallang.fixtures.CartFixture.generateIdGenerator;
import static io.mallang.fixtures.ProductFixture.generateProduct;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Import(TestConfig.class)
class AddCartItemUseCaseTest {

    @Test
    void 장바구니가_없으면_예외가_발생한다(
            @Autowired AddCartItemUseCase addCartItemUseCase
    ) {
        // given
        MemberId memberId = new MemberId("member-1");
        AddItemToCartCommand command = generateAddItemToCartCommand(
                memberId.value(),
                "product-1",
                2
        );

        // when & then
        assertThatThrownBy(() -> addCartItemUseCase.addItem(command))
                .isInstanceOf(CartNotFoundException.class);
    }

    @Test
    void 존재하지_않는_상품이면_예외가_발생한다(
            @Autowired AddCartItemUseCase addCartItemUseCase,
            @Autowired SaveCartPort saveCartPort
    ) {
        // given
        Cart cart = generateCart();
        saveCartPort.save(cart);
        AddItemToCartCommand command = generateAddItemToCartCommand(
                cart.getMemberId().value(),
                "product-1",
                2
        );

        // when & then
        assertThatThrownBy(() -> addCartItemUseCase.addItem(command))
                .isInstanceOf(ProductNotFoundException.class);
    }

    @Test
    void 재고보다_많은_수량을_장바구니에_담을_수_없다(
            @Autowired AddCartItemUseCase addCartItemUseCase,
            @Autowired SaveCartPort saveCartPort,
            @Autowired SaveProductPort saveProductPort
    ) {
        // given
        Cart cart = generateCart();
        Product product = generateProduct(4);

        cart.addItem(new AddCartItemCommand(product.getId(), 2), generateIdGenerator());

        saveCartPort.save(cart);
        saveProductPort.save(product);

        AddItemToCartCommand command = generateAddItemToCartCommand(
                cart.getMemberId().value(),
                product.getId().value(),
                3
        );

        // when & then
        assertThatThrownBy(() -> addCartItemUseCase.addItem(command))
                .isInstanceOf(InvalidValueException.class);
    }

    @Test
    void 기존_장바구니가_있으면_항목이_추가되어_저장된다(
            @Autowired AddCartItemUseCase addCartItemUseCase,
            @Autowired SaveCartPort saveCartPort,
            @Autowired LoadCartPort loadCartPort,
            @Autowired SaveProductPort saveProductPort
    ) {
        // given
        Cart cart = generateCart();
        Product product = generateProduct();

        saveCartPort.save(cart);
        saveProductPort.save(product);

        AddItemToCartCommand command = generateAddItemToCartCommand(
                cart.getMemberId().value(),
                product.getId().value(),
                2
        );

        // when
        AddItemToCartResult result = addCartItemUseCase.addItem(command);

        // then
        Cart loaded = loadCartPort.getByMemberId(cart.getMemberId());

        assertThat(result.cartItemId()).isNotBlank();
        assertThat(loaded.getItems())
                .hasSize(1)
                .extracting(CartItem::getQuantity)
                .containsExactly(2);
    }

    @Test
    void 이미_같은_상품이_있으면_수량이_합산되어_저장된다(
            @Autowired AddCartItemUseCase addCartItemUseCase,
            @Autowired SaveCartPort saveCartPort,
            @Autowired LoadCartPort loadCartPort,
            @Autowired SaveProductPort saveProductPort
    ) {
        // given
        Cart cart = generateCart();
        Product product = generateProduct();

        cart.addItem(new AddCartItemCommand(product.getId(), 2), generateIdGenerator());

        saveCartPort.save(cart);
        saveProductPort.save(product);

        AddItemToCartCommand command = generateAddItemToCartCommand(
                cart.getMemberId().value(),
                product.getId().value(),
                3
        );

        // when
        AddItemToCartResult result = addCartItemUseCase.addItem(command);

        // then
        Cart loaded = loadCartPort.getByMemberId(cart.getMemberId());

        assertThat(result.cartItemId()).isEqualTo(loaded.getItems().getFirst().getId().value());
        assertThat(loaded.getItems())
                .hasSize(1)
                .extracting(CartItem::getQuantity)
                .containsExactly(5);
    }
}
