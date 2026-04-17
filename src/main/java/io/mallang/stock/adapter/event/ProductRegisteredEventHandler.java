package io.mallang.stock.adapter.event;

import io.mallang.product.application.provided.command.model.ProductRegisteredEvent;
import io.mallang.product.domain.ProductId;
import io.mallang.stock.application.required.command.SaveStockPort;
import io.mallang.stock.domain.Stock;
import io.mallang.stock.domain.StockQuantity;
import io.mallang.stock.domain.command.CreateStockCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductRegisteredEventHandler {

    private final SaveStockPort saveStockPort;

    @EventListener
    public void handle(ProductRegisteredEvent event) {
        Stock stock = Stock.create(new CreateStockCommand(
                new ProductId(event.productIdValue()),
                new StockQuantity(event.stockQuantity())
        ));

        saveStockPort.save(stock);
    }
}
