package io.mallang.product.domain.exception;

import io.mallang.common.domain.exception.DomainNotFoundException;
import io.mallang.product.domain.ProductImageId;

public class ProductImageNotFoundException extends DomainNotFoundException {

    public ProductImageNotFoundException(ProductImageId productImageId) {
        super("상품 이미지를 찾을 수 없습니다.", "ProductImage를 찾을 수 없습니다 => id: " + productImageId.value());
    }
}
