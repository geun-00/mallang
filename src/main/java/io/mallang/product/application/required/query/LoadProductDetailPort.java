package io.mallang.product.application.required.query;

import io.mallang.product.application.provided.query.model.ProductDetailView;

public interface LoadProductDetailPort {

    ProductDetailView load(String productIdValue);
}
