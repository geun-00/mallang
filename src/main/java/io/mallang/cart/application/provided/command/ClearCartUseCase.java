package io.mallang.cart.application.provided.command;

import io.mallang.cart.application.provided.command.model.ClearCartCommand;

public interface ClearCartUseCase {

    void clear(ClearCartCommand command);
}
