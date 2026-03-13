package io.mallang.product.domain;

import java.math.BigDecimal;
import java.util.List;

public record ProductCreateCommand(
        String name,
        String description,
        BigDecimal price,
        int stockQuantity,
        String category,
        List<ProductImageCommand> images
) {
}
