package io.mallang.product.domain.command;

import java.math.BigDecimal;
import java.util.List;

public record CreateProductCommand(
        String name,
        String description,
        BigDecimal price,
        int stockQuantity,
        String category,
        List<CreateProductImageCommand> images
) {
}
