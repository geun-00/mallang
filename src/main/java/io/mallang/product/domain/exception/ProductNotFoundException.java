package io.mallang.product.domain.exception;

import io.mallang.common.domain.exception.DomainNotFoundException;
import io.mallang.product.domain.ProductId;

import java.util.List;
import java.util.stream.Collectors;

public class ProductNotFoundException extends DomainNotFoundException {

    public ProductNotFoundException(ProductId productId) {
        super("Product를 찾을 수 없습니다. id: " + productId.value());
    }

    public ProductNotFoundException(List<ProductId> productIds) {
        super("Product를 찾을 수 없습니다. ids: " + productIds.stream()
                                                              .map(ProductId::value)
                                                              .collect(Collectors.joining(", ")));
    }
}
