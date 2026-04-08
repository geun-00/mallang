package io.mallang.product.adapter.web.model;

import java.math.BigDecimal;
import java.util.List;

public record ProductDetailResponse(
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
        List<ProductImageResponse> images
) {
    public record ProductImageResponse(
            String imageId,
            String imageUrl,
            boolean thumbnail
    ) {
    }
}
