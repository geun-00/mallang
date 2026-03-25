package io.mallang.product.application.provided.command;

import io.mallang.product.application.provided.command.model.AddStockCommand;

public interface AddStockUseCase {

    void addStock(AddStockCommand command);
}
