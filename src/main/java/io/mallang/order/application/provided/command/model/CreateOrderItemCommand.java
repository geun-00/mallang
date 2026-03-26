package io.mallang.order.application.provided.command.model;

import java.math.BigDecimal;

public record CreateOrderItemCommand(
        String productId,
        int quantity,
        BigDecimal price
) {
}
