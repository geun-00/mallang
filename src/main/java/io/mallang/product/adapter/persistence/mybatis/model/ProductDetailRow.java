package io.mallang.product.adapter.persistence.mybatis.model;

import java.math.BigDecimal;

public record ProductDetailRow(
        String productId,
        String sellerIdValue,
        String sellerNickname,
        String name,
        String description,
        BigDecimal price,
        int stockQuantity,
        String status,
        String category,
        String imageId,
        String imageUrl,
        Boolean thumbnail
) {
}
