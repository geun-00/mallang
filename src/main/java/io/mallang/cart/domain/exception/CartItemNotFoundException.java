package io.mallang.cart.domain.exception;

import io.mallang.cart.domain.CartItemId;
import io.mallang.domain.common.exception.DomainNotFoundException;

public class CartItemNotFoundException extends DomainNotFoundException {

    public CartItemNotFoundException(CartItemId cartItemId) {
        super("장바구니 상품을 찾을 수 없습니다. cartItemId=" + cartItemId.value());
    }
}

