package io.mallang.product.domain;

public record ModifyProductCommand(
        String name,
        String description,
        int price,
        String category
) {
}
