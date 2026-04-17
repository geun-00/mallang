package io.mallang.stock.application.provided.command;

import io.mallang.stock.application.provided.command.model.AddStockCommand;

public interface AddStockUseCase {

    void addStock(AddStockCommand command);
}
