package io.mallang.cart.application.required.command;

import io.mallang.cart.domain.Cart;

public interface SaveCartPort {

    void save(Cart cart);
}
