package io.mallang.product.domain;

public record ProductImage(ProductImageId id, ImageUrl imageUrl) {

    public ProductImage {
        if (id == null)
            throw new IllegalArgumentException("ProductImageId는 null일 수 없습니다.");
        if (imageUrl == null)
            throw new IllegalArgumentException("ImageUrl은 null일 수 없습니다.");
    }
}
