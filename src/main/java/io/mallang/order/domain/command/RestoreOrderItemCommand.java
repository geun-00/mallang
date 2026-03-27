package io.mallang.order.domain.command;

import io.mallang.domain.common.vo.Money;
import io.mallang.order.domain.OrderItemId;
import io.mallang.product.domain.ProductId;

public record RestoreOrderItemCommand(
        OrderItemId id,
        ProductId productId,
        int quantity,
        Money price
) {
}
