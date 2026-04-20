package io.mallang.test.stock.application.provided.command;

import io.mallang.stock.application.provided.command.DeductStockUseCase;
import io.mallang.stock.application.provided.command.model.DeductStockCommand;

class StockCommandFacade {

    private final DeductStockUseCase deductStockUseCase;

    public StockCommandFacade(DeductStockUseCase deductStockUseCase) {
        this.deductStockUseCase = deductStockUseCase;
    }

    public synchronized void deductStock(DeductStockCommand command) {
        deductStockUseCase.deductStock(command);
    }
}

