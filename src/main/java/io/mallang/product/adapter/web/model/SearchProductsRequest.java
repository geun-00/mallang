package io.mallang.product.adapter.web.model;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record SearchProductsRequest(
        String sellerNickname,
        String productName,
        @PositiveOrZero BigDecimal minPrice,
        @PositiveOrZero BigDecimal maxPrice,
        String category,
        String lastProductId,
        @Positive Integer size
) {
    private static final int DEFAULT_SIZE = 20;

    public int sizeOrDefault() {
        return size == null ? DEFAULT_SIZE : size;
    }
}
