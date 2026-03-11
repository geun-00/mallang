package io.mallang.product.domain;

public enum ProductStatus {
    ON_SALE,
    SOLD_OUT,
    DISCONTINUED;

    public static ProductStatus of(StockQuantity stockQuantity) {
        if (stockQuantity.value() >= 1) {
            return ON_SALE;
        }

        return SOLD_OUT;
    }
}
