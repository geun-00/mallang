package io.mallang.fixtures;

import io.mallang.product.domain.Product;
import io.mallang.product.domain.ProductId;
import io.mallang.stock.domain.Stock;
import io.mallang.stock.domain.StockQuantity;
import io.mallang.stock.domain.command.CreateStockCommand;

public class StockFixture {

    public static Stock createStock(Integer quantity) {
        return generateStock(new ProductId("product-id"), quantity);
    }

    public static Stock generateStock(Product product, Integer quantity) {
        return generateStock(product.getId(), quantity);
    }

    public static Stock generateStock(ProductId productId, Integer quantity) {
        return Stock.create(new CreateStockCommand(
                productId,
                new StockQuantity(quantity)
        ));
    }
}
