package io.mallang.product.application.provided.command.model;

public record DeductStockCommand(
        String memberIdValue,
        String productIdValue,
        int quantity
) {
}
