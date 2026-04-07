package io.mallang.product.application.provided.query.model;

import java.math.BigDecimal;
import java.util.List;

public record ProductDetailView(
        String productId,
        String sellerIdValue,
        String sellerNickname,
        String name,
        String description,
        BigDecimal price,
        int stockQuantity,
        String status,
        String category,
        String thumbnailImageUrl,
        List<ProductImageView> images
) {
    public record ProductImageView(
            String imageId,
            String imageUrl,
            boolean thumbnail
    ) {
    }
}
