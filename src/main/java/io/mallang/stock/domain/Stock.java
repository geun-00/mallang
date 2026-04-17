package io.mallang.stock.domain;

import io.mallang.product.domain.ProductId;
import io.mallang.stock.domain.command.CreateStockCommand;
import io.mallang.stock.domain.command.RestoreStockCommand;
import lombok.Getter;

@Getter
public class Stock {

    private final ProductId productId;

    private StockQuantity quantity;

    private Stock(ProductId productId, StockQuantity quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

    public static Stock create(CreateStockCommand command) {
        return new Stock(command.productId(), command.quantity());
    }

    public static Stock restore(RestoreStockCommand command) {
        return new Stock(command.productId(), command.quantity());
    }

    public void add(int additionalStock) {
        this.quantity = this.quantity.add(additionalStock);
    }

    public void deduct(int deductedStock) {
        this.quantity = this.quantity.deduct(deductedStock);
    }

    public void checkAvailable(int quantity) {
        this.quantity.checkAvailable(quantity);
    }
}
