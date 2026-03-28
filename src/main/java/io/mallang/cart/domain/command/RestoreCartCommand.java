package io.mallang.cart.domain.command;

import io.mallang.cart.domain.CartItem;
import io.mallang.member.domain.MemberId;

import java.util.List;

public record RestoreCartCommand(
        MemberId memberId,
        List<CartItem> items
) {
}
