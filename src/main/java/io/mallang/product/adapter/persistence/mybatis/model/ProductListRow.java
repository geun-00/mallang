package io.mallang.product.adapter.persistence.mybatis.model;

import java.math.BigDecimal;

public record ProductListRow(
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
