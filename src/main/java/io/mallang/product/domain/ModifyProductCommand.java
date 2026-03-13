package io.mallang.product.domain;

import java.math.BigDecimal;

public record ModifyProductCommand(
        String name,
        String description,
        BigDecimal price,
        String category
) {
}
