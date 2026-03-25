package io.mallang.product.adapter.web.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

public record CreateProductRequest(
        @NotBlank String name,
        @NotNull String description,
        @NotNull BigDecimal price,
        @NotNull Integer stockQuantity,
        @NotBlank String category,
        List<ProductImageRequest> images
) {
    public record ProductImageRequest(String imageUrl, boolean isThumbnail) {
    }
}
