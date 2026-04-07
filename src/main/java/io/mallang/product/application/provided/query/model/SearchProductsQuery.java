package io.mallang.product.application.provided.query.model;

import java.math.BigDecimal;

public record SearchProductsQuery(
        String sellerNickname,
        String productName,
        BigDecimal minPrice,
        BigDecimal maxPrice,
        String category,
        String lastProductId,
        int size
) {
    public SearchProductsQuery {
        if (size < 1) {
            throw new IllegalArgumentException("size는 1 이상이어야 합니다.");
        }
        if (minPrice != null && maxPrice != null && minPrice.compareTo(maxPrice) > 0) {
            throw new IllegalArgumentException("minPrice는 maxPrice보다 클 수 없습니다.");
        }
    }
}
