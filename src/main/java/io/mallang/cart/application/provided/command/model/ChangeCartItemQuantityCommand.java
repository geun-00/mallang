package io.mallang.cart.application.provided.command.model;

public record ChangeCartItemQuantityCommand(
        String memberIdValue,
        String cartItemIdValue,
        int quantity
) {
}
