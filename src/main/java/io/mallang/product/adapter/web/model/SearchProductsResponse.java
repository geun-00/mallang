package io.mallang.product.adapter.web.model;

import java.math.BigDecimal;
import java.util.List;

public record SearchProductsResponse(
        List<ProductSummary> items,
        boolean hasNext,
        String nextCursor
) {
    public record ProductSummary(
            String productId,
            String sellerNickname,
            String name,
            BigDecimal price,
            int stockQuantity,
            String status,
            String category,
            String thumbnailImageUrl
    ) {
    }
}
