package io.mallang.product.adapter.persistence.mybatis.model;

import java.math.BigDecimal;

public record SearchProductCondition(
        String sellerNickname,
        String productName,
        BigDecimal minPrice,
        BigDecimal maxPrice,
        String category,
        String lastProductId,
        int limit
) {
}
