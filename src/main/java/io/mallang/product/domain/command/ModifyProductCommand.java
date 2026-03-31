package io.mallang.product.domain.command;

import io.mallang.domain.common.vo.Money;
import io.mallang.product.domain.ProductCategory;
import io.mallang.product.domain.ProductDescription;
import io.mallang.product.domain.ProductName;

public record ModifyProductCommand(
        ProductName name,
        ProductDescription description,
        Money price,
        ProductCategory category
) {
}
