package io.mallang.product.adapter.web.model;

import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record SearchProductsRequest(
        String sellerNickname,
        String productName,
        @PositiveOrZero BigDecimal minPrice,
        @PositiveOrZero BigDecimal maxPrice,
        String category
) {
}
