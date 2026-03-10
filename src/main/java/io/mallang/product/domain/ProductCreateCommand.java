package io.mallang.product.domain;

import java.util.List;

public record ProductCreateCommand(
        String name,
        String description,
        int price,
        int stockQuantity,
        String category,
        List<ProductImageCommand> images
) {
}
