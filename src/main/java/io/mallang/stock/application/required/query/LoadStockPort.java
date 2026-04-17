package io.mallang.stock.application.required.query;

import io.mallang.product.domain.ProductId;
import io.mallang.stock.domain.Stock;

import java.util.List;

public interface LoadStockPort {

    Stock getByProductId(ProductId productId);

    List<Stock> getAllByProductIds(List<ProductId> productIds);
}
