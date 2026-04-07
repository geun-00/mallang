package io.mallang.product.adapter.web.model;

import io.mallang.application.shared.query.SliceResult;
import io.mallang.product.application.provided.query.model.ProductListView;

import java.math.BigDecimal;
import java.util.List;

public record SearchProductsResponse(
        List<ProductSummary> items,
        boolean hasNext,
        String nextCursor
) {
    public static SearchProductsResponse from(SliceResult<ProductListView> result) {
        return new SearchProductsResponse(
                result.items()
                      .stream()
                      .map(item -> new ProductSummary(
                              item.productId(),
                              item.sellerNickname(),
                              item.name(),
                              item.price(),
                              item.stockQuantity(),
                              item.status(),
                              item.category(),
                              item.thumbnailImageUrl()
                      ))
                      .toList(),
                result.hasNext(),
                result.nextCursor()
        );
    }

    public record ProductSummary(
            String productId,
            String sellerNickname,
            String name,
            BigDecimal price,
            int stockQuantity,
            String status,
            String category,
            String thumbnailImageUrl
    ) {
    }
}
