package io.mallang.product.application.provided.command.model;

public record DiscontinueProductCommand(
        String memberIdValue,
        String productIdValue
) {
}
