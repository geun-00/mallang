package io.mallang.cart.application.provided.command;

import io.mallang.cart.application.provided.command.model.AddItemToCartCommand;
import io.mallang.cart.application.provided.command.model.AddItemToCartResult;

public interface AddCartItemUseCase {

    AddItemToCartResult addItem(AddItemToCartCommand command);
}
