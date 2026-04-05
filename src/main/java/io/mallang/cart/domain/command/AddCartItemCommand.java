package io.mallang.cart.domain.command;

public record AddCartItemCommand(
        String productId,
        int quantity
) {
}
