package io.mallang.product.application.provided.command.model;

public record ProductRegisteredEvent(
        String productIdValue,
        int stockQuantity
) {
}
