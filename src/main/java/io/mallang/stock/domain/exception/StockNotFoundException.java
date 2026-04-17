package io.mallang.stock.domain.exception;

import io.mallang.common.domain.exception.DomainNotFoundException;
import io.mallang.product.domain.ProductId;

import java.util.List;

import static java.util.stream.Collectors.joining;

public class StockNotFoundException extends DomainNotFoundException {

    public StockNotFoundException(ProductId productId) {
        super(
                "재고를 찾을 수 없습니다.",
                "Stock을 찾을 수 없습니다 => productId: " + productId.value()
        );
    }

    public StockNotFoundException(List<ProductId> productIds) {
        super(
                "재고를 찾을 수 없습니다.",
                "Stock을 찾을 수 없습니다 => productIds: " + productIds.stream()
                                                               .map(ProductId::value)
                                                               .collect(joining(", "))
        );
    }
}
