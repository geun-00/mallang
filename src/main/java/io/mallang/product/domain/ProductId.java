package io.mallang.product.domain;

import io.mallang.common.domain.exception.InvalidValueException;

public record ProductId(String value) {

    public ProductId {
        if (value == null || value.isBlank()) {
            throw new InvalidValueException("ProductId는 비어있을 수 없습니다.");
        }
    }
}
