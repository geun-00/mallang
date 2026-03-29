package io.mallang.cart.application.provided.command.model;

public record RemoveCartItemCommand(
        String memberIdValue,
        String cartItemIdValue
) {
}
