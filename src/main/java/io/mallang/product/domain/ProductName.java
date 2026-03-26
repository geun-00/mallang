package io.mallang.product.domain;

import io.mallang.domain.common.exception.InvalidValueException;

public record ProductName(String value) {

    public ProductName {
        if (value == null || value.isBlank()) {
            throw new InvalidValueException("상품 이름은 null 또는 빈 문자열이 될 수 없습니다.");
        }
        if (!value.equals(value.strip())) {
            throw new InvalidValueException("상품 이름은 공백으로 시작하거나 끝날 수 없습니다.");
        }
        if (value.length() > 100) {
            throw new InvalidValueException("상품 이름은 100자 이하여야 합니다.");
        }
    }
}
