package io.mallang.product.domain;

import io.mallang.common.domain.exception.InvalidValueException;

public record ProductImage(ProductImageId id, ImageUrl imageUrl) {

    public ProductImage {
        if (id == null) {
            throw new InvalidValueException("ProductImageId는 null일 수 없습니다.");
        }
        if (imageUrl == null) {
            throw new InvalidValueException("ImageUrl은 null일 수 없습니다.");
        }
    }
}
