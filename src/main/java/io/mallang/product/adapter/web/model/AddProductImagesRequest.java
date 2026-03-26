package io.mallang.product.adapter.web.model;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record AddProductImagesRequest(
        @NotNull List<String> imageUrls
) {
}
