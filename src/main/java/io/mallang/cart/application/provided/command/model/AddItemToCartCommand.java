package io.mallang.cart.application.provided.command.model;

public record AddItemToCartCommand(
        String memberIdValue,
        String productIdValue,
        int quantity
) {
}
