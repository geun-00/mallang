package io.mallang.product.application.provided.query;

import io.mallang.product.application.provided.query.model.ProductDetailView;

public interface GetProductDetailUseCase {

    ProductDetailView get(String productIdValue);
}
