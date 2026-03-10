package io.mallang.product.domain;

public record ProductImageId(String value) {

    public ProductImageId {
        if (value == null || value.isBlank())
            throw new IllegalArgumentException("ProductImageId는 비어있을 수 없습니다.");
    }
}
