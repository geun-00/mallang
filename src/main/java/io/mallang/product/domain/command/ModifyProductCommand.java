package io.mallang.product.domain.command;

import java.math.BigDecimal;

public record ModifyProductCommand(
        String name,
        String description,
        BigDecimal price,
        String category
) {
}
