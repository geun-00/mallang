package io.mallang.product.domain.command;

import io.mallang.common.domain.vo.Money;
import io.mallang.product.domain.ProductCategory;
import io.mallang.product.domain.ProductDescription;
import io.mallang.product.domain.ProductName;

import java.util.List;

public record CreateProductCommand(
        ProductName name,
        ProductDescription description,
        Money price,
        ProductCategory category,
        List<CreateProductImageCommand> images
) {
}
