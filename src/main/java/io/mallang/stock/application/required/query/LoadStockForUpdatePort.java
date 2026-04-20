package io.mallang.stock.application.required.query;

import io.mallang.product.domain.ProductId;
import io.mallang.stock.domain.Stock;

import java.util.List;

public interface LoadStockForUpdatePort {

    Stock getByProductIdForUpdate(ProductId productId);

    List<Stock> getAllByProductIdsForUpdate(List<ProductId> productIds);
}
