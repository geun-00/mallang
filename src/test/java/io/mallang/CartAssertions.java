package io.mallang;

import io.mallang.cart.domain.Cart;
import io.mallang.cart.domain.CartItem;
import org.assertj.core.api.ThrowingConsumer;

import static org.assertj.core.api.Assertions.assertThat;

public class CartAssertions {

    public static ThrowingConsumer<Cart> isSameAs(Cart expected) {
        return actual -> {
            assertThat(actual.getMemberId()).isEqualTo(expected.getMemberId());
            assertThat(actual.getItems()).hasSize(expected.getItems().size());

            for (int i = 0; i < expected.getItems().size(); i++) {
                assertThat(actual.getItems().get(i)).satisfies(isSameAs(expected.getItems().get(i)));
            }
        };
    }

    public static ThrowingConsumer<CartItem> isSameAs(CartItem expected) {
        return actual -> {
            assertThat(actual.getId()).isEqualTo(expected.getId());
            assertThat(actual.getProductId()).isEqualTo(expected.getProductId());
            assertThat(actual.getQuantity()).isEqualTo(expected.getQuantity());
        };
    }
}
