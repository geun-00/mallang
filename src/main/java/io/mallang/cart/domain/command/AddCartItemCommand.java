package io.mallang.cart.domain.command;

import io.mallang.product.domain.ProductId;

public record AddCartItemCommand(
        ProductId productId,
        int quantity
) {
}
