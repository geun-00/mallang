package io.mallang.product.domain.command;

import io.mallang.common.domain.vo.Money;
import io.mallang.member.domain.MemberId;
import io.mallang.product.domain.*;

import java.util.List;

public record RestoreProductCommand(
        ProductId id,
        MemberId sellerId,
        ProductName name,
        ProductDescription description,
        Money price,
        ProductStatus status,
        ProductCategory category,
        ProductImage thumbnailImage,
        List<ProductImage> images,
        boolean imagesLoaded
) {
}
