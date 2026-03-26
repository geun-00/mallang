package io.mallang.product.adapter.web.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record AddProductImagesRequest(
        @NotNull List<@NotBlank String> imageUrls
) {
}
