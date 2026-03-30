package io.mallang.cart.adapter.web.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ChangeCartItemQuantityRequest(
        @NotNull @Positive Integer quantity
) {
}
