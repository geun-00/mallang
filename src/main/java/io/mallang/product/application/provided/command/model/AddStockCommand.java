package io.mallang.product.application.provided.command.model;

public record AddStockCommand(
        String memberIdValue,
        String productIdValue,
        int quantity
) {
}
