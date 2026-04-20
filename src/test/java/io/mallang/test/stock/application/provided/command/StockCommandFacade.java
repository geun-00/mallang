package io.mallang.test.stock.application.provided.command;

import io.mallang.stock.application.provided.command.DeductStockUseCase;
import io.mallang.stock.application.provided.command.model.DeductStockCommand;

class StockCommandFacade {

    private static final int MAX_RETRY_COUNT = 10;

    private final DeductStockUseCase deductStockUseCase;

    public StockCommandFacade(DeductStockUseCase deductStockUseCase) {
        this.deductStockUseCase = deductStockUseCase;
    }

    public void deductStock(DeductStockCommand command) throws InterruptedException {
        for (int attempt = 1; attempt <= MAX_RETRY_COUNT; attempt++) {
            try {
                deductStockUseCase.deductStock(command);
                return;
            } catch (Exception exception) {
                Thread.sleep(50);
                if (attempt == MAX_RETRY_COUNT) {
                    throw exception;
                }
            }
        }
    }
}

