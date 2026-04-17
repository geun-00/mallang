package io.mallang.test.cart.domain;

import io.mallang.annotations.DomainTest;
import io.mallang.cart.domain.Cart;
import io.mallang.cart.domain.CartItem;
import io.mallang.cart.domain.CartItemId;
import io.mallang.cart.domain.command.AddCartItemCommand;
import io.mallang.cart.domain.exception.CartItemNotFoundException;
import io.mallang.common.domain.exception.InvalidValueException;
import io.mallang.common.domain.port.IdGenerator;
import io.mallang.member.domain.MemberId;
import io.mallang.product.domain.ProductId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.UUID;

import static io.mallang.fixtures.CartFixture.generateAddCartItemCommand;
import static io.mallang.fixtures.CartFixture.generateCart;
import static io.mallang.fixtures.CartFixture.generateCartWithItem;
import static io.mallang.fixtures.CommonFixture.generateIdGenerator;
import static io.mallang.fixtures.MemberFixture.generateMemberId;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DomainTest
@DisplayName("Cart 엔티티")
class CartTest {

    @Nested
    class 생성 {

        @Test
        void 유효한_MemberId로_장바구니를_생성할_수_있다() {
            MemberId memberId = generateMemberId();

            Cart cart = Cart.create(memberId);

            assertThat(cart.getMemberId()).isEqualTo(memberId);
        }

        @Test
        void 장바구니를_생성하면_항목_목록이_비어있다() {
            Cart cart = generateCart();

            assertThat(cart.getItems()).isEmpty();
        }
    }

    @Nested
    class 항목_추가 {

        @Test
        void 새로운_상품을_추가하면_CartItem이_생성된다() {
            Cart cart = generateCart();

            cart.addItem(generateAddCartItemCommand(), generateIdGenerator());

            assertThat(cart.getItems()).hasSize(1);
        }

        @Test
        void 이미_담긴_상품을_추가하면_새_CartItem이_생성되지_않고_수량이_합산된다() {
            Cart cart = generateCart();
            String productId = UUID.randomUUID().toString();
            cart.addItem(new AddCartItemCommand(new ProductId(productId), 2), generateIdGenerator());

            cart.addItem(new AddCartItemCommand(new ProductId(productId), 3), generateIdGenerator());

            assertThat(cart.getItems())
                    .hasSize(1)
                    .extracting(CartItem::getQuantity)
                    .containsExactly(5);
        }

        @Test
        void 서로_다른_상품을_추가하면_각각_별도의_CartItem으로_추가된다() {
            Cart cart = generateCart();

            cart.addItem(generateAddCartItemCommand(), generateIdGenerator());
            cart.addItem(generateAddCartItemCommand(), generateIdGenerator());

            assertThat(cart.getItems()).hasSize(2);
        }

        @ParameterizedTest
        @ValueSource(ints = {0, -1})
        void 수량이_0_이하이면_예외가_발생한다(int invalidQuantity) {
            Cart cart = generateCart();

            AddCartItemCommand command = generateAddCartItemCommand(invalidQuantity);
            IdGenerator idGenerator = generateIdGenerator();

            assertThatThrownBy(() -> cart.addItem(command, idGenerator))
                    .isInstanceOf(InvalidValueException.class);
        }
    }

    @Nested
    class 수량_변경 {

        @Test
        void 수량을_변경하면_CartItem의_수량이_변경된다() {
            Cart cart = generateCart();
            CartItemId itemId = cart.addItem(generateAddCartItemCommand(), generateIdGenerator());

            cart.changeQuantity(itemId, 10);

            assertThat(cart.getItems())
                    .extracting(CartItem::getQuantity)
                    .containsExactly(10);
        }

        @Test
        void 존재하지_않는_CartItemId로_수량을_변경하면_예외가_발생한다() {
            Cart cart = generateCart();

            CartItemId notExistCartItemId = new CartItemId(UUID.randomUUID().toString());

            assertThatThrownBy(() -> cart.changeQuantity(notExistCartItemId, 1))
                    .isInstanceOf(CartItemNotFoundException.class);
        }

        @ParameterizedTest
        @ValueSource(ints = {0, -1})
        void 변경_수량이_0_이하이면_예외가_발생한다(int invalidQuantity) {
            Cart cart = generateCart();
            CartItemId itemId = cart.addItem(generateAddCartItemCommand(), generateIdGenerator());

            assertThatThrownBy(() -> cart.changeQuantity(itemId, invalidQuantity))
                    .isInstanceOf(InvalidValueException.class);
        }
    }

    @Nested
    class 항목_제거 {

        @Test
        void CartItem을_제거할_수_있다() {
            Cart cart = generateCart();
            CartItemId itemId = cart.addItem(generateAddCartItemCommand(), generateIdGenerator());
            cart.addItem(generateAddCartItemCommand(), generateIdGenerator());

            cart.removeItem(itemId);

            assertThat(cart.getItems())
                    .hasSize(1)
                    .extracting(CartItem::getId)
                    .doesNotContain(itemId);
        }

        @Test
        void 존재하지_않는_CartItemId로_제거하면_예외가_발생한다() {
            Cart cart = generateCart();

            CartItemId notExistCartItemId = new CartItemId(UUID.randomUUID().toString());

            assertThatThrownBy(() -> cart.removeItem(notExistCartItemId))
                    .isInstanceOf(CartItemNotFoundException.class);
        }

        @Test
        void 여러_CartItem을_한_번에_제거할_수_있다() {
            Cart cart = generateCart();
            CartItemId firstId = cart.addItem(generateAddCartItemCommand(), generateIdGenerator());
            CartItemId secondId = cart.addItem(generateAddCartItemCommand(), generateIdGenerator());
            CartItemId thirdId = cart.addItem(generateAddCartItemCommand(), generateIdGenerator());

            cart.removeItems(List.of(firstId, secondId));

            assertThat(cart.getItems())
                    .hasSize(1)
                    .extracting(CartItem::getId)
                    .containsExactly(thirdId);
        }

        @Test
        void 여러_CartItem_제거_중_존재하지_않는_ID가_포함되면_아무것도_제거되지_않는다() {
            Cart cart = generateCart();
            CartItemId firstId = cart.addItem(generateAddCartItemCommand(), generateIdGenerator());
            CartItemId secondId = cart.addItem(generateAddCartItemCommand(), generateIdGenerator());
            CartItemId thirdId = cart.addItem(generateAddCartItemCommand(), generateIdGenerator());

            CartItemId notExistCartItemId = new CartItemId(UUID.randomUUID().toString());
            List<CartItemId> itemIds = List.of(firstId, notExistCartItemId);

            assertThatThrownBy(() -> cart.removeItems(itemIds))
                    .isInstanceOf(CartItemNotFoundException.class);

            assertThat(cart.getItems())
                    .hasSize(3)
                    .extracting(CartItem::getId)
                    .containsExactly(firstId, secondId, thirdId);
        }

        @Test
        void 빈_목록으로_호출하면_예외_없이_정상_처리된다() {
            Cart cart = generateCartWithItem(2);

            cart.removeItems(List.of());

            assertThat(cart.getItems()).hasSize(2);
        }
    }

    @Nested
    class 비우기 {

        @Test
        void 전체_비우기를_하면_CartItems가_비어있다() {
            Cart cart = generateCartWithItem(3);

            cart.clear();

            assertThat(cart.getItems()).isEmpty();
        }

        @Test
        void 이미_비어있는_장바구니에서_전체_비우기를_해도_예외_없이_정상_처리된다() {
            Cart cart = generateCart();

            cart.clear();

            assertThat(cart.getItems()).isEmpty();
        }
    }

    @Nested
    class 조회 {

        @Test
        void 담긴_상품_ID_목록을_반환한다() {
            Cart cart = generateCart();

            ProductId productId1 = new ProductId(UUID.randomUUID().toString());
            ProductId productId2 = new ProductId(UUID.randomUUID().toString());
            cart.addItem(new AddCartItemCommand(productId1, 1), generateIdGenerator());
            cart.addItem(new AddCartItemCommand(productId2, 1), generateIdGenerator());

            List<ProductId> productIds = cart.getProductIds();

            assertThat(productIds).containsExactlyInAnyOrder(productId1, productId2);
        }

        @Test
        void 비어있는_장바구니에서_상품_ID_목록을_조회하면_빈_목록이_반환된다() {
            Cart cart = generateCart();

            assertThat(cart.getProductIds()).isEmpty();
        }
    }
}
