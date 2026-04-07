package io.mallang.product.adapter.web.model;

import io.mallang.product.application.provided.query.model.ProductDetailView;

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
    public static ProductDetailResponse from(ProductDetailView view) {
        return new ProductDetailResponse(
                view.productId(),
                view.sellerIdValue(),
                view.sellerNickname(),
                view.name(),
                view.description(),
                view.price(),
                view.stockQuantity(),
                view.status(),
                view.category(),
                view.thumbnailImageUrl(),
                view.images().stream()
                    .map(image -> new ProductImageResponse(
                            image.imageId(),
                            image.imageUrl(),
                            image.thumbnail()
                    ))
                    .toList()
        );
    }

    public record ProductImageResponse(
            String imageId,
            String imageUrl,
            boolean thumbnail
    ) {
    }
}
