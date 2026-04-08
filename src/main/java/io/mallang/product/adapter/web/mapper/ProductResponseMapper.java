package io.mallang.product.adapter.web.mapper;

import io.mallang.application.shared.query.SliceResult;
import io.mallang.product.adapter.web.model.ProductDetailResponse;
import io.mallang.product.adapter.web.model.SearchProductsResponse;
import io.mallang.product.application.provided.query.model.ProductDetailView;
import io.mallang.product.application.provided.query.model.ProductListView;

import static io.mallang.product.adapter.web.model.ProductDetailResponse.ProductImageResponse;
import static io.mallang.product.adapter.web.model.SearchProductsResponse.ProductSummary;

public final class ProductResponseMapper {

    private ProductResponseMapper() {
    }

    public static SearchProductsResponse toSearchProductsResponse(SliceResult<ProductListView> result) {
        return new SearchProductsResponse(
                result.items()
                      .stream()
                      .map(ProductResponseMapper::toProductSummary)
                      .toList(),
                result.hasNext(),
                result.nextCursor()
        );
    }

    public static ProductDetailResponse toProductDetailResponse(ProductDetailView view) {
        return new ProductDetailResponse(
                view.productId(),
                view.sellerIdValue(),
                view.sellerNickname(),
                view.name(),
                view.description(),
                view.price(),
                view.stockQuantity(),
                view.status(),
                view.category(),
                view.thumbnailImageUrl(),
                view.images()
                    .stream()
                    .map(image -> new ProductImageResponse(
                            image.imageId(),
                            image.imageUrl(),
                            image.thumbnail()
                    ))
                    .toList()
        );
    }

    private static ProductSummary toProductSummary(ProductListView item) {
        return new ProductSummary(
                item.productId(),
                item.sellerNickname(),
                item.name(),
                item.price(),
                item.stockQuantity(),
                item.status(),
                item.category(),
                item.thumbnailImageUrl()
        );
    }
}
