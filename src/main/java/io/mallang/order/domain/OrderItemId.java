package io.mallang.order.domain;

import io.mallang.common.domain.exception.InvalidValueException;

public record OrderItemId(String value) {

    public OrderItemId {
        if (value == null || value.isBlank()) {
            throw new InvalidValueException("OrderItemId는 비어있을 수 없습니다.");
        }
    }
}
