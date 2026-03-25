package io.mallang.product.domain.exception;

import io.mallang.domain.common.exception.DomainNotFoundException;
import io.mallang.product.domain.ProductId;

public class ProductNotFoundException extends DomainNotFoundException {

    public ProductNotFoundException(ProductId productId) {
        super("Product를 찾을 수 없습니다. id: " + productId.value());
    }
}
