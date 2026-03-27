package io.mallang.order.domain;

import java.math.BigDecimal;

public record PlaceOrderItemCommand(String productId, int quantity, BigDecimal price) {
}
