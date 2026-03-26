package io.mallang.product.domain.exception;

import io.mallang.domain.common.exception.DomainNotFoundException;
import io.mallang.product.domain.ProductImageId;

public class ProductImageNotFoundException extends DomainNotFoundException {

    public ProductImageNotFoundException(ProductImageId productImageId) {
        super("ProductImage를 찾을 수 없습니다. id: " + productImageId.value());
    }
}
