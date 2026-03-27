package io.mallang.order.application.provided.command.model;

public record CreateOrderItemCommand(
        String productId,
        int quantity
) {
}
