package io.mallang.cart.application.required.query;

import io.mallang.cart.domain.Cart;
import io.mallang.cart.domain.exception.CartNotFoundException;
import io.mallang.member.domain.MemberId;

public interface LoadCartPort {

    /**
     * @throws CartNotFoundException
     */
    Cart getByMemberId(MemberId memberId);
}
