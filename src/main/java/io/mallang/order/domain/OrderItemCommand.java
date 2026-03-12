package io.mallang.order.domain;

public record OrderItemCommand(String productId, int quantity, int price) {
}
