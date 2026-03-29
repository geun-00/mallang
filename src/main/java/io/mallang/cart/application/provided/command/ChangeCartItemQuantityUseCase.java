package io.mallang.cart.application.provided.command;

import io.mallang.cart.application.provided.command.model.ChangeCartItemQuantityCommand;

public interface ChangeCartItemQuantityUseCase {

    void changeQuantity(ChangeCartItemQuantityCommand command);
}
