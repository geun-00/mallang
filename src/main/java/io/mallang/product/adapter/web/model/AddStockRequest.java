package io.mallang.product.adapter.web.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record AddStockRequest(
        @NotNull @Positive Integer quantity
) {
}
