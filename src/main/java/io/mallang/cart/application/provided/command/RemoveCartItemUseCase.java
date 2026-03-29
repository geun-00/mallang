package io.mallang.cart.application.provided.command;

import io.mallang.cart.application.provided.command.model.RemoveCartItemCommand;

public interface RemoveCartItemUseCase {

    void removeItem(RemoveCartItemCommand command);
}
