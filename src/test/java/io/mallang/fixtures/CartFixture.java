package io.mallang.fixtures;

import io.mallang.cart.application.provided.command.model.AddItemToCartCommand;
import io.mallang.cart.domain.command.AddCartItemCommand;
import io.mallang.cart.domain.Cart;
import io.mallang.cart.domain.CartItemId;
import io.mallang.common.domain.port.IdGenerator;
import io.mallang.member.domain.MemberId;
import io.mallang.product.domain.ProductId;

import java.util.UUID;

public class CartFixture {

    public static IdGenerator generateIdGenerator() {
        return () -> UUID.randomUUID().toString();
    }

    public static MemberId generateMemberId() {
        return new MemberId(UUID.randomUUID().toString());
    }

    public static Cart generateCart() {
        return Cart.create(generateMemberId());
    }

    public static AddCartItemCommand generateAddCartItemCommand() {
        return generateAddCartItemCommand(1);
    }

    public static AddCartItemCommand generateAddCartItemCommand(int quantity) {
        return new AddCartItemCommand(new ProductId(UUID.randomUUID().toString()), quantity);
    }

    public static AddItemToCartCommand generateAddItemToCartCommand(String memberIdValue, String productIdValue, int quantity) {
        return new AddItemToCartCommand(memberIdValue, productIdValue, quantity);
    }

    public static Cart generateCartWithItem(int count) {
        Cart cart = generateCart();
        for (int i = 0; i < count; i++) {
            cart.addItem(generateAddCartItemCommand(), generateIdGenerator());
        }
        return cart;
    }

    public static CartItemId generateNotExistCartItemId() {
        return new CartItemId(UUID.randomUUID().toString());
    }
}
