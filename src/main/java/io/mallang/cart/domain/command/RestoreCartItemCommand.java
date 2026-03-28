package io.mallang.cart.domain.command;

import io.mallang.cart.domain.CartItemId;
import io.mallang.product.domain.ProductId;

public record RestoreCartItemCommand(
        CartItemId id,
        ProductId productId,
        int quantity
) {
}
