package io.mallang.product.adapter.persistence.mybatis;

import io.mallang.application.shared.query.SliceResult;
import io.mallang.product.adapter.persistence.mybatis.model.SearchProductCondition;
import io.mallang.product.application.provided.query.model.ProductListView;
import io.mallang.product.application.provided.query.model.SearchProductsQuery;
import io.mallang.product.application.required.query.SearchProductsPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ProductQueryPersistenceAdapter implements SearchProductsPort {

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
                                                              .map(row -> new ProductListView(
                                                                      row.productId(),
                                                                      row.sellerIdValue(),
                                                                      row.sellerNickname(),
                                                                      row.name(),
                                                                      row.price(),
                                                                      row.stockQuantity(),
                                                                      row.status(),
                                                                      row.category(),
                                                                      row.thumbnailImageUrl()
                                                              ))
                                                              .toList();

        boolean hasNext = loadedItems.size() > query.size();
        List<ProductListView> items = hasNext
                ? loadedItems.subList(0, query.size())
                : loadedItems;
        String nextCursor = hasNext ? items.getLast().productId() : null;

        return new SliceResult<>(items, hasNext, nextCursor);
    }
}
