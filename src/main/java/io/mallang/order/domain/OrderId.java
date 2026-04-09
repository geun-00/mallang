package io.mallang.order.domain;

import io.mallang.common.domain.exception.InvalidValueException;

public record OrderId(String value) {

    public OrderId {
        if (value == null || value.isBlank()) {
            throw new InvalidValueException("OrderId는 비어있을 수 없습니다.");
        }
    }
}
