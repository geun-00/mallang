package io.mallang.product.adapter.web.model;

import jakarta.validation.Valid;
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
        @Valid List<@NotNull @Valid ProductImageRequest> images
) {
    public record ProductImageRequest(
            @NotBlank String imageUrl,
            boolean isThumbnail
    ) {
    }
}
