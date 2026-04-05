package io.mallang.product.application.required.query;

import io.mallang.product.domain.Product;
import io.mallang.product.domain.ProductId;
import io.mallang.product.domain.exception.ProductNotFoundException;

import java.util.List;

public interface LoadProductPort {

    /**
     * @throws ProductNotFoundException
     */
    Product getById(ProductId productId);

    /**
     * @throws ProductNotFoundException
     */
    Product getByIdWithImages(ProductId productId);

    /**
     * @throws ProductNotFoundException
     */
    List<Product> getAllByIds(List<ProductId> productIds);
}
