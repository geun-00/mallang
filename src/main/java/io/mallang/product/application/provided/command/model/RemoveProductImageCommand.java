package io.mallang.product.application.provided.command.model;

public record RemoveProductImageCommand(
        String memberIdValue,
        String productIdValue,
        String productImageIdValue
) {
}
