package io.mallang.product.application.provided.command;

import io.mallang.product.application.provided.command.model.DeductStockCommand;

public interface DeductStockUseCase {

    void deductStock(DeductStockCommand command);
}
