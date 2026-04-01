package io.mallang.product.domain.command;

import io.mallang.domain.common.vo.Money;
import io.mallang.product.domain.ProductCategory;
import io.mallang.product.domain.ProductDescription;
import io.mallang.product.domain.ProductName;
import io.mallang.product.domain.StockQuantity;

import java.util.List;

public record CreateProductCommand(
        ProductName name,
        ProductDescription description,
        Money price,
        StockQuantity stockQuantity,
        ProductCategory category,
        List<CreateProductImageCommand> images
) {
}
