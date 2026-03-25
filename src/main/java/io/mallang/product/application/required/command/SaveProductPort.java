package io.mallang.product.application.required.command;

import io.mallang.product.domain.Product;

public interface SaveProductPort {

    void save(Product product);
}
