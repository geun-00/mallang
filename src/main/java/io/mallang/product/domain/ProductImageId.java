package io.mallang.product.domain;

import io.mallang.common.domain.exception.InvalidValueException;

public record ProductImageId(String value) {

    public ProductImageId {
        if (value == null || value.isBlank()) {
            throw new InvalidValueException("ProductImageId는 비어있을 수 없습니다.");
        }
    }
}
