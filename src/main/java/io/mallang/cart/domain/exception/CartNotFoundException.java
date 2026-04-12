package io.mallang.cart.domain.exception;

import io.mallang.common.domain.exception.DomainNotFoundException;
import io.mallang.member.domain.MemberId;

public class CartNotFoundException extends DomainNotFoundException {

    public CartNotFoundException(MemberId memberId) {
        super("장바구니를 찾을 수 없습니다.", "Cart를 찾을 수 없습니다 => memberId: " + memberId.value());
    }
}
