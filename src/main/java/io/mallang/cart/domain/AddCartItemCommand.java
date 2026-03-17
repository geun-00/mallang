package io.mallang.cart.domain;

public record AddCartItemCommand(
        String productId,
        int quantity
) {
}
