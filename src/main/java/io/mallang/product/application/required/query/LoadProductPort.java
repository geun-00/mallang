package io.mallang.product.application.required.query;

import io.mallang.product.domain.Product;
import io.mallang.product.domain.ProductId;

public interface LoadProductPort {

    Product getById(ProductId productId);

    Product getByIdWithImages(ProductId productId);
}
