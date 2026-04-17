package io.mallang.stock.domain.command;

import io.mallang.product.domain.ProductId;
import io.mallang.stock.domain.StockQuantity;

public record RestoreStockCommand(
        ProductId productId,
        StockQuantity quantity
) {
}
