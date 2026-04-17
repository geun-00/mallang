package io.mallang.stock.application.provided.command;

import io.mallang.stock.application.provided.command.model.DeductStockCommand;

public interface DeductStockUseCase {

    void deductStock(DeductStockCommand command);
}
