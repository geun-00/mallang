package io.mallang.product.application.provided.command.model;

import java.math.BigDecimal;

public record UpdateProductCommand(
        String memberIdValue,
        String productIdValue,
        String name,
        String description,
        BigDecimal price,
        String category
) {
}
