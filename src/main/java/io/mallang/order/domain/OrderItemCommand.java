package io.mallang.order.domain;

import java.math.BigDecimal;

public record OrderItemCommand(String productId, int quantity, BigDecimal price) {
}
