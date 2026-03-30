package io.mallang.member.domain;

import io.mallang.domain.common.exception.InvalidValueException;

public record ShippingAddressId(String value) {

    public ShippingAddressId {
        if (value == null || value.isBlank()) {
            throw new InvalidValueException("ShippingAddressId는 비어있을 수 없습니다.");
        }
    }
}
