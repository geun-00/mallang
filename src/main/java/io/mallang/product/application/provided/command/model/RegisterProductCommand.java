package io.mallang.product.application.provided.command.model;

import java.math.BigDecimal;
import java.util.List;

public record RegisterProductCommand(
        String sellerIdValue,
        String name,
        String description,
        BigDecimal price,
        int stockQuantity,
        String category,
        List<RegisterProductImageCommand> images
) {
}
