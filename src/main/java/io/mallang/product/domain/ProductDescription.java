package io.mallang.product.domain;

public record ProductDescription(String value) {

    public ProductDescription {
        if (value == null)
            throw new IllegalArgumentException("상품 이름은 null이 될 수 없습니다.");
        if (value.length() > 2000)
            throw new IllegalArgumentException("상품 이름은 2,000자 이하여야 합니다.");

        value = value.strip();
    }
}
