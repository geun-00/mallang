package io.mallang.product.application.provided.query.model;

import java.math.BigDecimal;

public record ProductListView(
        String productId,
        String sellerIdValue,
        String sellerNickname,
        String name,
        BigDecimal price,
        int stockQuantity,
        String status,
        String category,
        String thumbnailImageUrl
) {
}
