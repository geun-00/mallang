package io.mallang.product.adapter.web.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record UpdateProductRequest(
        @NotBlank String name,
        @NotNull String description,
        @NotNull BigDecimal price,
        @NotBlank String category
) {
}
