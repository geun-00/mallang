package io.mallang.cart.adapter.web.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record AddCartItemRequest(
        @NotBlank String productId,
        @NotNull @Positive Integer quantity
) {
}
