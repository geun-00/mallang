package io.mallang.order.domain.command;

import io.mallang.common.domain.vo.Money;
import io.mallang.product.domain.ProductId;

public record PlaceOrderItemCommand(
        ProductId productId,
        int quantity,
        Money price
) {
}
