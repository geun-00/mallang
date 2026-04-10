package io.mallang.cart.domain;

import io.mallang.common.domain.exception.InvalidValueException;

public record CartItemId(String value) {

    public CartItemId {
        if (value == null || value.isBlank()) {
            throw new InvalidValueException("CartItemId는 비어있을 수 없습니다.");
        }
    }
}
