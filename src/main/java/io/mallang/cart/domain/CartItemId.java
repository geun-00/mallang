package io.mallang.cart.domain;

public record CartItemId(String value) {

    public CartItemId {
        if (value == null || value.isBlank())
            throw new IllegalArgumentException("CartItemId는 비어있을 수 없습니다.");
    }
}
