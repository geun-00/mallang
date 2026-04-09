package io.mallang.product.adapter.persistence.mybatis;

import io.mallang.common.applicaiton.query.SliceResult;
import io.mallang.product.adapter.persistence.mybatis.model.ProductDetailRow;
import io.mallang.product.adapter.persistence.mybatis.model.ProductListRow;
import io.mallang.product.adapter.persistence.mybatis.model.SearchProductCondition;
import io.mallang.product.application.provided.query.model.ProductDetailView;
import io.mallang.product.application.provided.query.model.ProductListView;
import io.mallang.product.application.provided.query.model.SearchProductsQuery;
import io.mallang.product.application.required.query.LoadProductDetailPort;
import io.mallang.product.application.required.query.SearchProductsPort;
import io.mallang.product.domain.ProductId;
import io.mallang.product.domain.exception.ProductNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static io.mallang.product.application.provided.query.model.ProductDetailView.ProductImageView;

@Repository
@RequiredArgsConstructor
public class ProductQueryPersistenceAdapter implements SearchProductsPort, LoadProductDetailPort {

    private final ProductQueryMapper productQueryMapper;

    @Override
    public SliceResult<ProductListView> search(SearchProductsQuery query) {
        SearchProductCondition condition = new SearchProductCondition(
                query.sellerNickname(),
                query.productName(),
                query.minPrice(),
                query.maxPrice(),
                query.category(),
                query.lastProductId(),
                query.size() + 1
        );

        List<ProductListView> loadedItems = productQueryMapper.selectProducts(condition)
                                                              .stream()
                                                              .map(this::convertToView)
                                                              .toList();

        return SliceResult.of(loadedItems, query.size(), ProductListView::productId);
    }

    private ProductListView convertToView(ProductListRow row) {
        return new ProductListView(
                row.productId(),
                row.sellerIdValue(),
                row.sellerNickname(),
                row.name(),
                row.price(),
                row.stockQuantity(),
                row.status(),
                row.category(),
                row.thumbnailImageUrl()
        );
    }

    @Override
    public ProductDetailView load(String productIdValue) {
        List<ProductDetailRow> rows = productQueryMapper.selectProductDetailRows(productIdValue);
        if (rows.isEmpty()) {
            throw new ProductNotFoundException(new ProductId(productIdValue));
        }

        ProductDetailRow first = rows.getFirst();

        List<ProductImageView> images = rows.stream()
                                            .filter(row -> row.imageId() != null)
                                            .map(row -> new ProductImageView(
                                                    row.imageId(),
                                                    row.imageUrl(),
                                                    Boolean.TRUE.equals(row.thumbnail())
                                            ))
                                            .toList();

        String thumbnailImageUrl = rows.stream()
                                       .filter(row -> Boolean.TRUE.equals(row.thumbnail()))
                                       .map(ProductDetailRow::imageUrl)
                                       .findFirst()
                                       .orElse(null);

        return new ProductDetailView(
                first.productId(),
                first.sellerIdValue(),
                first.sellerNickname(),
                first.name(),
                first.description(),
                first.price(),
                first.stockQuantity(),
                first.status(),
                first.category(),
                thumbnailImageUrl,
                images
        );
    }
}
